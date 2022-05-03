[![CI](https://github.com/dzavolskaya/zrule/actions/workflows/scala.yml/badge.svg?branch=main)](https://github.com/shafranka/zrule/actions/workflows/scala.yml)
# zrule

## Introduction

Zrule is (an attempt of :blush: ) a generic Business Rules Engine with Domain Specific Language. 
It can be customised with your own rules and be considered as “decision as a service”.

(Side note: :peace_symbol: _Z is just the first letter of my last name and [_Drools_](https://www.drools.org) already exists._)

It offers the following features:

- language for defining business rules validated by its own compiler
- deployment environment that supports versioning and coexistence between multiple clients
- solver
- REST API for communication with third-party applications

### Language:

```
when <condition1> and <condition2> and <condition3> then <decision> end
```

where:

- **when**: is the condition list location declaration keyword
- **and**: is the keyword that separates 2 conditions (therefore optional in the case of a single condition)
- **then**: is the decision location declaration keyword

*Condition* is the expression that allows the engine to check if a fact has the appropriate value to select a decision.

```
fact operator value
```

The zrule engine language supports the following operations (with their associated operator):

| Operator | Operation |
| -------- | --------- | 
| eq | equals |
| ne | not equals |
| lt | lower than |
| lte | lower than or equals |
| gt | greater than |
| gte | greater than or equals |


*Decision*

```
fact is value
```

To specify the type of a value, use the following syntactic rules:

| Types | Syntax | Example |
| ------| ------ |  ------ |
| string | “\<value\>” | “hello” |
| number | \<value\> (decimal separator .) | 2.18 |
| boolean | true\|false |  |
| null | none |  |	

For example,

```
when sleep_hour lte 6 and anxiety eq none then insomnia_treatment is \"melissa dream: 2 tables before sleep\" end
```

## Setup

- Install java 8 (at least)
- Install scala 2.13 (http://www.scala-lang.org/download/)
- Install sbt (https://www.scala-sbt.org/1.x/docs/Setup.html)

### Compile

In the root, run:

```
$ sbt -mem 8000
> clean; update; compile
```

### Test

```
> test
```

For the simplicity attempt, embedded postgres is started and closed in database tests.

### Run

In  order to run the application locally, make sure to install and start local postgresql server (or else update database config) with created deployment table.

```
create table if not exists deployment(
    id serial not null,
    primary key (id),
    name varchar(50) NOT null,
    tenant varchar(50) NOT null,
    version int not null,
    date timestamp not null,
    code text not null
);
```

Then use:

```
> run
```
