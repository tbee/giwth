# GiWTh ('giveth')
A small library for structuring unit / integration tests written in Java, inspired by Cucumber / Gherkin. 
Usually an example explains more than a thousand words, so let's start with that:

```java
Scenario.of("Modify Vacation Hours", new MyContext())

        .given( RosterPeriod.exists().startingOn("2022-09-19") )
        .and( User.isLoggedin("peter") )

        .when( Overview.isAccessed() )
        .and( VacationHours.isSetTo(20).forUser("peter").onDate("2022-09-19") )

        .then( VacationHours.shouldBe(20).forUser("peter").onDate("2022-09-19") )
        .and( WeekTotals.shouldBe(20,0,0,0,0,0).forUser("peter").inRosterPeriod("2022-09-19") )
        .and( RunningWeekTotals.shouldBe(20,20,20,20,20,20).forUser("peter").inRosterPeriod("2022-09-19") )
        .and( Event.exists().who("peter").what("SetVacationHours").user("peter").rosterPeriod("2022-09-19").detailSubstring("hours=20") );
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
User.isLoggedin("user")
```

So if this so closely resembles Cucumber, what is the added value? 
Well, Cucumber has a few issues, and one of the most disturbing ones is that the regular expressions used to match sentences in the feature file to Java methods is a bit fragile.
Also Cucumber does not clearly distinguish between step definitions for Given / When / Then, everything needs to be done with these regular expressions.
Giwth completely eliminates the regular expressions and uses the step definitions directly in the scenario.
A short explanation of the reasons behind Giwth can be found in this [blog](https://www.tbee.org/2023/02/19/given-when-then/).

## Step definitions
The scenario requires implementations of specific interfaces, Given, When, Then, for each of the three phases.
How these implementations are created, is up to you.
All three interfaces even are identical, they all only have one method `Context run(Context context)`.

But as explained in the [blog](https://www.tbee.org/2023/02/19/given-when-then/), there is a suggestion: use builders.
Normal builders have a `newBuilder` method, then some parameters are provided in a fluent API style, and at the end there is a `build` method.
Again an example says more than a 1000 words:

```java
User.of("peter").isLoggedin(); // returns a Given
User.of("peter").logsIn(); // returns a When
User.of("peter").shouldBeLoggedin(); // returns a Then
```
In these examples the `of` is the `newBuilder`, and for example the `isLoggedin` is the `build`.

And the only way this User step builder can be used in a scenario is this:
```java
Scenario.of(...)
        .given( User.of("gwen").isLoggedin() )
        .when( User.of("peter").logsIn() )
        .then( User.of("peter").shouldBeLoggedin() )
```

Unlike Cucumber you cannot (accidentally) mix the step definitions, they are strongly typed.

However, in real world use with complexer steps it turns out this builder pattern feels a bit unnatural. 
The builder pattern on its own is okay, but the `newBuilder`-`build` is difficult to get right.
If you merge the structure of the builder with the Giwth interfaces, it becomes easier:
since each Given/WhenThen interface only contains a single `run` method, that can naturally become the `build` method.
The static method on the class simply always returns an implementation of Given/When/Then, which optionally can be a custom class with setters for the parameters. 

So for example:
```java
VacationHours.isSetTo(20).forUser("peter").onDate("2022-09-19")
```

Is implemented like:
```java
class VacationHours {
    public static isSetTo(int hours) {
        return new IsSetTo(hours);
    }

    public static class IsSetTo implements When<MyContext> {
        private final int hours;
        private String forUser;
        private LocalDate onDate;

        public IsSetTo(int hours) {
            this.hours = hours;
        }

        public IsSetTo forUser(String v) {
            this.forUser = v;
        }

        public IsSetTo onDate(String v) {
            this.onDate = LocalDate.parse(v);
        }

        @Override
        public void run(MyContext myContext) {
            // use hours, forUser and onDate hete to do what needs to be done.
        }
    }
}
```

This is a simpel and straight forward way of implementing steps.

For simple steps an extra class is not needed:
```java
class VacationHours {
    public static When<MyContext> shouldBe(int hours) {
        return myContext -> {
            // use hours here
        };
    }
}
```

## Context
The step definitions are implemented by the user, and somehow they need to have access to the environment they are executed in.
This is done using a context.
Things like repositories, access to browser control, etc. is provided through the context.

```java
Scenario.of("Modify Vacation Hours", new MyContext()) // The context is provided here 

...

class User {
    
    public Given<MyContext> isLoggedIn(String username) {
        return new Given<MyContext>() {
            @Override
            public void run(MyContext myContext) {  // Is made available here
                ...
            }
        };
    }
    
    public When<MyContext> logsIn(String username) {
        return myContext -> { // Writing it as a lamba makes it more readable
            ...
        };
    }
}
```

The context is also strongly typed: each step definition method specifies what class it expects as the argument, so it makes sense to use a single Context class for all tests to allow maximum reusability of the steps.
The context class can be manipulated by the steps, storing data for future reference, or for use by other steps.

## Background
Like Gherkin, Giwth supports setting up a background preceding every test:

```java
@BeforeEach
public void beforeEach() {
    Scenario.background(new MyContext())
            .given( RosterPeriod.exists().startingOn("2022-09-19") )
            .and( User.isLoggedin("peter") )
}

@Test
public void someTest(){
    Scenario.<MyContext>of("Modify Vacation Hours")
            .when( Overview.isAccessed() )
            .and( VacationHours.isSetTo(20).forUser("peter").onDate("2022-09-19") )
            ...
}
```

Background requires using the combination of background(context) with of(description) factory methods.
Background is handled using thread-local storage, so multiple tests can run in parallel.

## Data Tables
Giwth does have initial support for Cucumber data tables.
You can write something like this:

```java
Scenario.of("basicTable", stepContext)
        .given(Users.exist(
               """
               | firstName | lastName | age |
               | Donald    | Duck     | 40  |
               | Mickey    | Mouse    | 45  |
               | Dagobert  | Duck     | 60  |
               """))
```

And then implement that using the TableProcessor:

```java
static public Given<StepContext> exist(String table) {
    return stepContext -> {
        List<User> users = new ArrayList<>();
        
        new TableProcessor<User>()
            .onLineStart(i -> new User())
            .onLineEnd((i, user) -> users.add(user))
        
            .onField("firstName", (user, v) -> user.firstName(v))
            .onField("lastName", (user, v) -> user.lastName(v))
            .onField("age", (user, v) -> user.age(Integer.parseInt(v)))
        
            .process(table);
    };
}
```

However, one of the ideas of GiWTh is being compile time checkable, tables are not.
Matching of the column headers in the onField methods is fragile, those are not checked at compile time.
Value string to actual type conversion (e.g. integer for age) can have conversion problems (e.g. NumberFormatException).
But on the other hand, any failures happen at test time, which is probably good enough; it at least is not at runtime.

Alternatively a table could be implemented like so:

```java
Scenario.of("basicTable", stepContext)
        .given(User.firstName("Donald")  .lastName("Duck") .age(40).exists())
        .and(  User.firstName("Mickey")  .lastName("Mouse").age(45).exists())
        .and(  User.firstName("Dagobert").lastName("Duck") .age(60).exists());
```

For you to decide what is preferable.

If you need to escape characters in the data table, normal Java escapes can be used.
Except, of course, for the pipe symbol itself: a \\| will cause errors, so you need to use a double pipe "||". 

## Cucumber outline and examples
Cucumber supports parameterized tests using outline and an examples block.

```gherkin
Scenario Outline: Add two numbers <num1> & <num2>
  Given I have a calculator
  When I add <num1> and <num2>
  Then the result should be <total>

Examples:
  | num1 | num2 | total |
  |   10 |    6 |    16 |
  |   -4 |    2 |    -2 |
  |   -6 | -100 |  -106 |
```

Since GiWTh relies on an external testing framework like JUnit for execution, 
it is not in control over the execution, and cannot provide a similar construct.

But for example JUnit5's @CsvSource annotation can be made to look quite similar to data tables.  

```java
@DisplayName("Add two numbers:")
@ParameterizedTest(name = "{index} => {0}: ({1}, {2})")
@CsvSource(delimiter = '|', textBlock = """
    pos         | 10 |    6 |   16
    pos and neg | -4 |    2 |   -2        
    neg         | -6 | -100 | -106
    """)
public void sumTest(String description, int num1, int num2, int total) {
    Scenario.of("sumTest", stepContext)
        .given( Calculator.hasValue(num1) )
        .when( Calculator.add(num2) )
        .then( Calculator.valueShouldBe(total) );
}
```
This is so close that having GiWTh implement a special @DataTable extension for JUnit seems unnecessary.

## Sequence
Like Cucumber, Giwth allows the user to mix and match given, when and then; it does not enforce only three steps. 
The question of course is if it is wise to do that, but that is left to the discretion of the user.
However, the 'and' and 'but' following a given, when or then only accept the same type.

```java
Scenario.of(...)
        .given( User.isLoggedin("gwen") ) // a Given sequence is started
        .and( User.logsIn("peter") ) // ERROR, because 'and' expects a Given, not a When
        .but( User.shouldBeLoggedin("peter") ) // ERROR, because 'but' expects a Given, not a Then
        .when( ... ) // a When sequence is started
        ...
```


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
    <version>2.0.0</version>
    <scope>test</scope>
</dependency>
```

