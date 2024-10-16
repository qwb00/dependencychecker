# Dependency checker

This project is a Java application that checks if a provided list of JAR files contains all the necessary dependencies to execute a specified main class.

## Usage

To run the application, execute the following commands:

### Build the project
```shell 
./gradlew build
```

### Run the application
```shell
./gradlew runChecker -PappArgs="<mainClass>,<jarFiles>"
```

Where:
- `<mainClass>` The fully qualified name of the main class to check.
- `<jarFiles>` Comma-separated list of paths to the JAR files.

### Example
```shell
./gradlew runChecker -PappArgs="com.jetbrains.internship2024.ClassB,test_cases/build/libs/ModuleA-1.0.jar,test_cases/build/libs/ModuleB-1.0.jar"
```

## Test tasks.

The project includes predefined test cases that you can run using Gradle tasks.

### Run all tests

```shell
./gradlew runAllTests
```

### Run Individual Test Cases
    
```shell
./gradlew testCase1
./gradlew testCase2
# and so on...
```