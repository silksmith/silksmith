#silksmith
Compile your JS code with Google's Closure Compiler, generate CSS with SASS. Build it with Gradle!

[![Join the chat at https://gitter.im/silksmith/silksmith](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/silksmith/silksmith?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

JS, SCSS and stuff
##Concept
TBD
###Resource Types
####Statics
Statics are resoureces that are already in der distributable form and you would usually serve in a public directory and include them in your ```<script>```, ```<link rel="stylesheet">```. For example the jQuery library, a precompiled Bootstrap CSS or its icon fonts.
####JS - Closure Compiler conform
The JS sources in silksmith are JavaScript sources that will be "compiled" by the Google's Closure Compiler and come with valid [https://developers.google.com/closure/compiler/docs/js-for-compiler](JSDoc annotations) and ```goog.provide("my.Class")``` and ```goog.require("your.Class")``` calls.
####Externs
Externs are part of the part of the Closure Compiler minification process. They describe the interface how to interact with precompiled libraries. During the "compile" process the compiler will check if you call for example jQuery in the right way.

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

###Publishing
Silksmith publishes .silk packages on nexus via gradles publishing extension

```
publishing {
	publications {
		maven(MavenPublication) {
			groupId 'org.example.foogroup'
			from components.silk
		}
     }
}
```

####Publishing third party libraries
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
