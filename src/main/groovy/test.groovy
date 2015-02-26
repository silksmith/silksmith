def line = "date:'medium' \n lala"
println line.replace("\\", "\\\\").replaceAll( /'/, "\\\\'")