# GiWTh ('giveth')
A small library for structuring unit / integration tests written in Java, inspired by Cucumber / Gherkin. 
Usually an example explains more than a 1000 words, so let's start with that:

```java
Scenario.of("Modify Vacation Hours", new MyContext())

        .given( RosterPeriod.startingOn("2022-09-19").exists() )
        .and( User.of("peter").isLoggedin() )

        .when( Overview.isAccessed() )
        .and( VacationHours.forUser("peter").onDate("2022-09-19").isSetTo(20) )

        .then( VacationHours.forUser("peter").onDate("2022-09-19").shouldBe(20) )
        .and( WeekTotals.forUser("peter").inRosterPeriod("2022-09-19").shouldBe(20,0,0,0,0,0) )
        .and( RunningWeekTotals.forUser("peter").inRosterPeriod("2022-09-19").shouldBe(20,20,20,20,20,20) )
        .and( Event.who("peter").what("SetVacationHours").user("peter").rosterPeriod("2022-09-19").detailSubstring("hours=20").exists() );
```

What you see here is the Giwth equivalent of a Cucumber test which normally would be written in Gherkin. 
And as with that framework it consists of two parts. First the Given/When/Then frame

```java
Scenario.of(...)
        
        .given(...)
        .and(...)
        
        .when(...)
        .and(...)
        
        .then(...)
        .and(...)
```

And secondly the definitions of the steps, for example:

```java
User.of("user").isLoggedin()
```

So if this so closely resembles Cucumber, what is the added value? 
Well, Cucumber has a few issues, and one of the most disturbing ones is that the regular expressions used to match sentences in the feature file to Java methods is a bit fragile.
Also Cucumber does not clearly distinguish between step definitions for Given / When / Then, everything needs to be done with these regular expressions.
Giwth completely eliminates the regular expressions and uses the step definitions directly in the scenario.
A short explanation of the reasons behind Giwth can be found in this [blog](https://www.tbee.org/2023/02/19/given-when-then/).

## Step definitions
The scenario requires implementations of specific interfaces, Given, When, Then, for each of the three phases.
How you, the user of Giwth, create these implementations is up to you.
All three interfaces even are identical, they all only have one method `Context run(Context context)`.

But as explained in the [blog](https://www.tbee.org/2023/02/19/given-when-then/), there is a suggestion; use builders.
The builder focuses on a topic, provides some parameters for that topic, and then the resulting build method creates the actual step.
Again an example says more than a 1000 words:

```java
User.of("peter").isLoggedin(); // returns a Given
User.of("peter").logsIn(); // returns a When
User.of("peter").shouldBeLoggedin(); // returns a Then
```

So the only way this specific step builder can be used in a scenario is this:

```java
Scenario.of(...)
        .given( User.of("gwen").isLoggedin() )
        .when( User.of("peter").logsIn() )
        .then( User.of("peter").shouldBeLoggedin() )
```

Unlike Cucumber you cannot (accidentally) mix the step definitions, they are strongly typed.

## Builder support
Writing builders is not trivial.
The regular expressions in Cucumber map onto arguments in the method call, which is a whole lot simpler than writing a complete builder.
But Giwth has you covered.

Consider this example of a step definition in Cucumber:

```java
public class CalculatorStepDefs {
    
    @When("^I add (-?\\d+) and (-?\\d+)$")
    public void testAdd(int num1, int num2) throws Throwable{
        // ...
    }
}
```

Using Giwth builder support, it's equivalent would be:

```java
@Step(stripSuffix = "StepDefs")
abstract public class CalculatorStepDefs {

    protected int num;

    public When<StepContext> add(int num2) {
        return stepContext -> {
            // ...
            return stepContext;
        };
    }
}

// Usage:
...when(Calculator.of().num(1).add(2))
```

Granted, it is not as compact as the Cucumber notation, but pratically -the code that needs to be typed- is not that far off.
And with more steps the difference becomes less, because no regexps are needed and the parameters can be reused. 
Also, the "abstract" and "protected" are more factual, but can be omitted.
And there are some additional annotations to tune the generated code (see below).

The basic usage pattern of a generated builder is:

```java
Step.of().stepParamN(x).action(actionArguments).actionParameterN(y);
```

A builder can become more complex, but still the actual typed code is fairly to the point: 

```java
@Step(stripSuffix = "Def")
public class StepDef {

    @Of // adds a static factory method ofStepParam()
    int stepParam;

    // will create a method action() derived from the class name
    public class Action implements When<StepContext> {

        @Arg // moves actionArg into the action() method argument list
        double actionArg;

        String actionParam;

        @Override
        public StepContext run(StepContext stepContext) {
            // ... all 3 variables are available to implement the step
            return stepContext;
        }
    }
}

// Usage:
...when(Step.ofStepParam(1).action(2.34).actionParam("with grace"))
```

Aynhow, just something to make life easier.
Maven will pick this up automatically, in an IDE annotation processing should be enabled.

## Context
The step definitions are implemented by the user and somehow they need to have access to the environment they are executed in.
This is done using a context.
Things like repositories, access to browser control, etc. is provided through the context.

```java
Scenario.of("Modify Vacation Hours", new MyContext()) // The context that is provided here 

...

class User {
    
    public Given<MyContext> isLoggedIn() {
        return new Given<MyContext>() {
            @Override
            public MyContext run(MyContext myContext) {  // Is made available here
                ...
                return myContext;
            }
        };
    }
    
    public When<MyContext> logsIn() {
        return myContext -> { // Writing it as a lamba makes it more readable
            ...
            return myContext;
        };
    }
}
```

The context is also strongly typed: each step definition method specifies what class it expects as the argument, so it makes sense to use a single Context class for all tests to allow maximum reusability of the steps.
The context class can be manipulated by the steps, storing data for future reference, or for use by other steps.

A step receives a context as an argument but also needs to return it, this allows Context to be implemented as immutable.
This is not a requirement, but Giwth enables the user to do so, if preferred.

## Background
Like Gherkin, Giwth supports setting up a background preceeding every test:

```java
@BeforeEach
public void beforeEach() {
    Scenario.background(new MyContext())
            .given( RosterPeriod.startingOn("2022-09-19").exists() )
            .and( User.of("peter").isLoggedin() )
}

@Test
public void someTest(){
    Scenario.<MyContext>of("Modify Vacation Hours")
            .when( Overview.isAccessed() )
            .and( VacationHours.forUser("peter").onDate("2022-09-19").isSetTo(20) )
            ...
}
```

## Data Tables
Giwth does not (yet?) have support for something like Cucumber data tables.
Parameterized tests, as provided by the testing framework, or standard Java records and lists can be used to replace them.

## Sequence
Like Cucumber, Giwth allows the user to mix and match given, when and then; it does not enforce only three steps. 
The question of course is if it is wise to do that, but that is left to the discretion of the user. 

## Compatibility
As long as you stick to Giwth's API, upgrading should not be too much of a hassle. If you start being extra creative, you're on your own ;-)

## Support
There is no formal support for Giwth: this library is an open source hobby project and no claims can be made. Asking for help is always an option. But so is participating, creating pull requests, and other ways of contributing.

## Usage
Just include a dependency in your project. For the latest version see [Maven central](https://central.sonatype.com/namespace/org.tbee.giwth)

```xml
<dependency>
    <groupId>org.tbee.giwth</groupId>
    <artifactId>giwth</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

