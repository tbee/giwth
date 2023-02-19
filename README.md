# Giwth ('give-ith')
A small library for structuring unit / integration tests written in Java, based on Cucumber / Gherkin. 
Usually an example explains more than a 1000 words, so let's start with that:

```java
Scenario.of("Modify Vacation Hours", context() )

        .given( RosterPeriod.startingOn("2022-09-19").exists() )
        .and( User.of("user").isLoggedin() )

        .when( Overview.isAccessed() )
        .and( VacationHours.forUser("user").onDate("2022-09-19").isSetTo(20) )

        .then( VacationHours.forUser("user").onDate("2022-09-19").shouldBe(20) )
        .and( WeekTotals.forUser("user").inRosterPeriod("2022-09-19").shouldBe(20,0,0,0,0,0) )
        .and( RunningWeekTotals.forUser("user").inRosterPeriod("2022-09-19").shouldBe(20,0,0,0,0,0) )
        .and( Event.who("user").what("SetVacationHours").user("user").rosterPeriod("2022-09-19").detailSubstring("hours=20").exists() );
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
Well, Cucumber has a few issues and one of the most distrubing ones is that the regular expressions used to match sentences in the feature file to Java methods is a bit, hm, fragile.
Also Cucumber does not clear distinguish between step definitions for Given / When / Then, every thing needs to be done within these regular expressions.
Maybe we can do better.