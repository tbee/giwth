# Giwth ('giveth')
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
        .and( RunningWeekTotals.forUser("peter").inRosterPeriod("2022-09-19").shouldBe(20,0,0,0,0,0) )
        .and( Event.who("peter").what("SetVacationHours").user("peter").rosterPeriod("2022-09-19").detailSubstring("hours=20").exists() );
```

What you see here is the Giwth equivalent of a Cucumber test which normally would be written in Gherkin. 
And as with that framework it consists of two parts. First the Given/When/Test frame

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
A little explanation of the reasons behind Giwth can be found in this [blog](https://www.tbee.org/2023/02/19/given-when-then/).

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
    
    public When<MyContext> isLoggedIn() {
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

## Data Tables
Giwth does not (yet?) have support for something like Cucumber data tables.
Parameterized tests, as provided by the testing framework, or standard Java records and lists can be used to replace them.

## Sequence
Like Cucumber, Giwth allows the user to mix and match given, when and then; it does not enforce only three steps. 
The question of course is if it is wise to do that, but that is left to the discretion of the user. 