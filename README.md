##silksmith
Compile your JS code with Google's Closure Compiler, generate CSS with SASS. Build it with Gradle!

[![Join the chat at https://gitter.im/silksmith/silksmith](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/silksmith/silksmith?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

JS, SCSS and stuff

##Setup
See this sample app to get started https://github.com/silksmith/sample-app

build.gradle
```

plugins {
	id "io.silksmith.plugin" version "0.3.3"
}

```
##JavaScript

###Dev
To compile the application specify the "entryPoint" in for the closureCompileJS task

```
closureCompileJS {
	entyPoint = "my.app.main"
}

```
In your ```index.html```
```
<html>
<head>

	<link href="/your-style-sheet.css">
	
</head>
<body>
	...
	<script src="your-app.js"></script>
</body>
</html>
```
Serve the directory that contains the ```index.html```
```
server {
	// for example if your index.html is in src/main/resources/static
	dir file("src/main/resources/static") 
}
```
###Dependencies
The main configuration is named ```web``` and for tests you can use ```testWeb```
```
repositories {
    maven { url="http://dl.bintray.com/silksmith-io/silk"} // here are some packages on bintray
}

dependencies {
    web "io.silksmith.libs:closure-base:1.0.0+smith.+" //goog lib (required)
    web "io.silksmith.libs:bootstrap-sass:3.3.3+smith.+" //lets use the sass sources of bootstrap
    web "io.silksmith.libs:jquery:1.11.2+smith.+" //jquery 
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
plugins {
	id "io.silksmith.plugin-angular" version "0.3.3"
}

```
Now you can put angular templates in ```src/main/ngTemplates``` and can annotate that need injection with ```@ngInject```

##Links
- Google hardcore JS "mininfier" https://developers.google.com/closure/compiler/
- SASS/SCSS http://sass-lang.com

##Other plugins:

Some initial ideas from https://github.com/eriwen/gradle-js-plugin
