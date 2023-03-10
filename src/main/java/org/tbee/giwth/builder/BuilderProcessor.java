package org.tbee.giwth.builder;

import com.google.auto.service.AutoService;
import org.tbee.giwth.builder.annotations.Arg;
import org.tbee.giwth.builder.annotations.Of;
import org.tbee.giwth.builder.annotations.Step;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// https://www.baeldung.com/java-annotation-processing-builder
// https://hannesdorfmann.com/annotation-processing/annotationprocessing101/

@SupportedAnnotationTypes("org.tbee.giwth.builder.annotations.Step")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        try {
            // Iterate over all to the processor registered annotations (only one)
            for (TypeElement annotation : annotations) {

                // Get all elements annotated by the current annotation
                Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(annotation);

                // Filter wrongly placed annotations
                List<? extends Element> annotatedClassElements = annotatedElements.stream()
                        .filter(ae -> { // Must be only class
                            boolean isClass = ae.getKind().isClass();
                            if (!isClass) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "The @org.tbee.giwth.builder.annotations.Step can only be applied to a class, not on: " + ((TypeElement) ae.getEnclosingElement()).getQualifiedName() + "." + ae, ae);
                            }
                            return isClass;
                        })
                        .toList();

                // Now process each class
                for (Element element : annotatedClassElements) {
                    Step stepAnnotation = element.getAnnotation(Step.class);
                    TypeElement classTypeElement = (TypeElement) element;
                    processStepAnnotation(stepAnnotation, classTypeElement);
                }
            }
            return true; // don't process there annotations by another processor
        }
        catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        return false;
    }

    private void processStepAnnotation(Step stepAnnotation, TypeElement classTypeElement) throws IOException {

        // Derive class name from annotated class
        String annotatedClassName = classTypeElement.getQualifiedName().toString();
        String className = annotatedClassName;
        if (className.endsWith(stepAnnotation.stripSuffix())) {
            className = className.substring(0, className.length() - stepAnnotation.stripSuffix().length());
        }

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String classSimpleName = className.substring(lastDot + 1);

        // Start writing the source file
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating " + className);
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(className);
        try (PrintWriter writer = new PrintWriter(builderFile.openWriter())) {

            // package
            if (packageName != null) {
                writer.print("package ");
                writer.print(packageName);
                writer.println(";");
                writer.println();
            }

            // class declaration
            writer.print("""
                         public class  %classSimpleName% extends %annotatedClassName% { 
                             
                         """
                    .replace("%classSimpleName%", classSimpleName)
                    .replace("%annotatedClassName%", annotatedClassName));

            // static of() method
            writer.print("""
                             static public %className% of() {
                                return new %className%();
                             }
                             
                         """
                    .replace("%className%", className));

            // process the class' contents
            for (Element element : classTypeElement.getEnclosedElements()) {
                if (element instanceof VariableElement variableElement) {
                    topicParameter(className, writer, variableElement);
                }
                if (element instanceof TypeElement subclassTypeElement) {
                    verbWithParametersOrArguments(className, writer, subclassTypeElement);
                }
            }

            // close class
            writer.println("}");
        }
    }

    private void topicParameter(String className, PrintWriter writer, VariableElement variableElement) {

        // Get info
        String variableType = variableElement.asType().toString();
        String variableName = variableElement.getSimpleName().toString();

        // Write method
        writer.print("""
                         public %className% %variableName%(%variableType% v) { 
                             this.%variableName% = v;
                             return this;
                         }
                         
                     """
                .replace("%className%", className)
                .replace("%variableType%", variableType)
                .replace("%variableName%", variableName));

        // If of-annotation is present, write static of-method
        Of ofAnnotation = variableElement.getAnnotation(Of.class);
        if (ofAnnotation != null) {
            String methodName = "of" + (ofAnnotation.name().length() > 0 ? ofAnnotation.name() : firstUpper(variableName));
            writer.print("""
                         static public %className% %methodName%(%variableType% v) { 
                             return of().%variableName%(v);
                         }
                         
                     """
                    .replace("%className%", className)
                    .replace("%variableType%", variableType)
                    .replace("%variableName%", variableName)
                    .replace("%methodName%", methodName));
        }
    }

    private void verbWithParametersOrArguments(String className, PrintWriter writer, TypeElement subclassTypeElement) {

        // Get class info
        String subclassName = subclassTypeElement.getQualifiedName().toString();
        String classSimpleName = subclassName.substring(subclassName.lastIndexOf('.') + 1);
        String implClassSimpleName = classSimpleName + "Impl";

        // Write the class, including parameters, but collect any encountered arguments (those need to go into the method)
        List<Argument> arguments = new ArrayList<>();
        writer.print("""
                         public class  %implClassSimpleName% extends %classSimpleName% { 
                             
                     """
                .replace("%implClassSimpleName%", implClassSimpleName)
                .replace("%classSimpleName%", classSimpleName));
        for (Element element : subclassTypeElement.getEnclosedElements()) {
            if (element instanceof VariableElement variableElement) {
                verbParameter(implClassSimpleName, writer, variableElement, arguments);
            }
        }
        writer.print("    }\n\n");

        // Write the method
        // First process the arguments
        String argumentDefinition = arguments.stream()
                 .map(a -> a.type + " " + a.name())
                 .collect(Collectors.joining(", "));
        String argumentAssignments = arguments.stream()
                .map(a -> "        giwth." + a.name + " = " + a.name() + ";")
                .collect(Collectors.joining("\n"));
        writer.print("""
                         public %implClassSimpleName% %methodName%(%argumentDefinition%) {
                             %implClassSimpleName% giwth = new %implClassSimpleName%();
                     %argumentAssignments%
                             return giwth;
                         }    
                     """
                .replace("%implClassSimpleName%", implClassSimpleName)
                .replace("%methodName%", firstLower(classSimpleName))
                .replace("%argumentDefinition%", argumentDefinition)
                .replace("%argumentAssignments%", argumentAssignments));
    }

    record Argument(String type, String name) {}

    private void verbParameter(String className, PrintWriter writer, VariableElement variableElement, List<Argument> methodArguments) {

        // Get info
        String variableType = variableElement.asType().toString();
        String variableName = variableElement.getSimpleName().toString();

        // Arg annotations moves the variable to the method parameters
        Arg argAnnotation = variableElement.getAnnotation(Arg.class);
        if (argAnnotation != null) {
            methodArguments.add(new Argument(variableType, variableName));
            return;
        }

        // Write method
        writer.print("""
                             public %className% %variableName%(%variableType% v) { 
                                  this.%variableName% = v;
                                 return this;
                             }
                         
                     """
                .replace("%className%", className)
                .replace("%variableType%", variableType)
                .replace("%variableName%", variableName));
    }

    private String firstUpper(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String firstLower(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }
}
