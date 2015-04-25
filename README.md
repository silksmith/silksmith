#silksmith
Compile your JS code with Google's Closure Compiler, generate CSS with SASS. Build it with Gradle!

[![Join the chat at https://gitter.im/silksmith/silksmith](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/silksmith/silksmith?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

JS, SCSS and stuff
##Concept
TBD
- Package JS and SCSS
- Compile JS (type checking)
- Compile SSS
- Builtin Dev/Test server
- Nothing needed on the build server, gradle comes with everything (no node/npm/bower etc must be installed or executed in the beginning)

###Resource Types
####Statics
Statics are resoureces that are already in there distributable form and you would usually serve them in a public directory and include them in your ```<script>```, ```<link rel="stylesheet">```. For example the jQuery library, a precompiled Bootstrap CSS or its icon fonts.
####JS - Closure Compiler conform
The JS sources in silksmith are JavaScript sources that will be "compiled" by the Google's Closure Compiler and come with valid [JSDoc annotations](https://developers.google.com/closure/compiler/docs/js-for-compiler) and ```goog.provide("my.Class")``` and ```goog.require("your.Class")``` calls.
####Externs
[Externs](https://developers.google.com/closure/compiler/docs/api-tutorial3) are part of the Closure Compiler minification process. They describe the interface how to interact with precompiled libraries. During the "compile" process the compiler will check if you call for example jQuery in the right way. So usually if you have a static js resource that you somehow will call from the closure js code you should provide the externs here.
####SCSS
Silksmith also supports [SASS](http://sass-lang.com/) with SCSS syntax. Packages that provide scss sources will automatically be in the SASS *include_path*
##Setup
See this sample app to get started https://github.com/silksmith/sample-app

build.gradle
```

plugins {
	id "io.silksmith.plugin" version "0.4.1"
}

```
###Sources
```
src/main/js - Closure JS
src/main/statics - Static Elements
src/main/externs - Externs for static JS libraries
```
If you want to package 3rd party libs like jQuery, underscore etc. don't copy them manually to the ```src/main/statics```, see this [section](#publishing-third-party-libraries)

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
	<link href="/your-style-sheet.css" rel="stylesheet">
</head>
<body>
	...
	<script src="your-app.js"></script>
</body>
</html>
```
For the CSS you can refer to any of your output css, so if you have ```my-style.scss``` you can use ```my-style.css```. Currently for the JS part you need to include the ```${project.name}.js```


Serve the directory that contains the ```index.html```. The build in server will provide all the js sources and will run sass in watch mode inside.
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
Like in other builds in gradle there is also a test configuration ```testWeb``` that extends the ```web``` configuration. So if you need to include test libraries like chai or sinon you can add them via the test configuration
```
dependencies {
    testWeb "io.silksmith.libs:chai:1.10.0+smith.0"
    testWeb "io.silksmith.libs:sinon:1.12.1+smith.0"
    testWeb "io.silksmith.libs:sinon-chai:2.6.0+smith.0"
}
```

Silksmith comes with a builtin mocha test runner, so you can run
``` gradle testJS ```
it will start a browser and will execute the tests. In the current version the test server uses the same port as the develop server so make sure you not running ```server``` while executing ```testIS```.
To run the server in watch mode append ```-Pwatch```.


####Testing on Saucelab
TBD

###Publishing
Silksmith publishes .silk packages on nexus via gradles publishing extension

```
apply plugin: 'maven-publish'
publishing {
	publications {
		maven(MavenPublication) {
			groupId 'org.example.foogroup'
			from components.silk
		}
     }
}
```

#### Publishing third party libraries
Silksmith comes with two helper class to package existing libraries like jQuery, Bootstrap etc.
#####DownloadFiles Task
If the library is provided somewhere in the web to download, you can define a download task and use its output as source dir.
````
task download(type: io.silksmith.task.DownloadFiles) {
    url = "http://sinonjs.org/releases/sinon-1.12.1.js"

}
silksmith.source {
    main {
        statics {
            srcDir download.outputs.files.singleFile
        }
    }
}
pack.dependsOn download
```
#####GitFiles Task
Very often the library resource is provided via a git repository, especially all libraries available via bower. Therefore you can use the *GitFiles* Task to specifiy the repo and the branch/tag/commit you want to checkout and also use its output as source directory

```
task github(type: io.silksmith.task.GitFiles) {
    url = "https://github.com/jquery/jquery.git"
    checkout = "1.11.2"
}

silksmith.source {
    main {
	    statics {
	        srcDir "$github.outputs.files.singleFile/dist"
	        include "jquery.min.js"
	        include "jquery.min.map"
	    }
    }
}
```
####Version
Since many packages may be 3rd party libraries but will come with externs we typically use the libraries version for the package plus an kind of build indicator to distingiuish fixes/updates on the externs e.g.:
```
version = "1.11.2+smith.1"
version = "1.11.2+smith.3"
...
```
####Internal structure of .silk packages

```
my-package-1.0.1+smith.0.silk/
- js/ #Closure JS Sources
- externs/ #Externs for Closure JS
- scss/ #SCSS 
- statics/ #Statics  
silk.json #Extra stuff for later :)

```
##Extension
###Angular
build.gradle
```
plugins {
	id "io.silksmith.plugin-angular" version "0.4.1"
}

```
Now you can put angular templates in ```src/main/ngTemplates``` and can annotate that need injection with ```@ngInject```

##Links
- Google hardcore JS "mininfier" https://developers.google.com/closure/compiler/
- SASS/SCSS http://sass-lang.com
- Gradle http://gradle.org

##Other plugins:

Some initial ideas from https://github.com/eriwen/gradle-js-plugin
