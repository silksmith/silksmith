def x = "margin-top: 4px\\9; }" =~ /(\w+(-\w+)*\s*:.*\\9;)/

if(x) {
	println "matches"
}
println x
println x.group(1)