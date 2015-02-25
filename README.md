##Silk Smith
JS, SCSS and stuff

##Setup
build.gradle
```
apply plugin : "silksmith"

```
##JavaScript

###Dev
To compile the application specify the "entryPoint" in for the closureCompileJS task

```

closureCompileJS {
	entyPoint = "my.app.main"
}

```

###Testing

Silksmith comes with a inbuild mocha test runner
``` gradle testJS ```
if you want to run the tests in watch mode append ```-Pwatch```



##Extension
###Angular
build.gradle
```
apply plugin : "silksmith-angular"

```


##Other plugins:

Some initial ideas from https://github.com/eriwen/gradle-js-plugin
