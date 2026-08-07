rem call mvnw versions:set
rem call mvnw jreleaser:config
rem call mvnw clean deploy jreleaser:full-release -DaltDeploymentRepository=local::file:./target/staging-deploy

rem https://medium.com/@othmane.outama/step-by-step-publishing-java-libraries-to-maven-central-abf2ffeb04c0

rem show keys
call gpg --list-keys --keyid-format 0xshort
call mvnw versions:set
call mvnw clean deploy -Prelease
pause