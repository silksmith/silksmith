//def line = "date:'medium' \n lala"
//println line.replace("\\", "\\\\").replaceAll( /'/, "\\\\'")


def providePattern = /(?m)^\s*goog\.provide\(\s*['"](.+)['"]\s*\)/
def modulePattern = /(?m)^\s*goog\.module\(\s*['"](.+)['"]\s*\)/
def requirePattern = /(?m)^\s*(?:(?:var|let|const)\s+[a-zA-Z\_\$][a-zA-Z0-9\$\_]*\s*=\s*)?goog\.require\(\s*[\'"](.+)[\'"]\s*\)/


println """public static final String PROVIDE_PATTERN = ""; """ 
println providePattern.toString()
println """public static final String MODULE_PATTERN = ""; """
println  modulePattern.toString()
println """public static final String REQUIRE_PATTERN = ""; """ 
println requirePattern.toString()
