##Silk Smith

[![Join the chat at https://gitter.im/silksmith/silksmith](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/silksmith/silksmith?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

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
Now you can put angular templates in ```src/main/ngTemplates``` and can annotate that need injection with ```@ngInject```



##Other plugins:

Some initial ideas from https://github.com/eriwen/gradle-js-plugin
