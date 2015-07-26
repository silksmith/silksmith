package io.silksmith.development.server.closure

import java.io.File;

interface ClosureJSOutput {
	File getDest()
	def getEntryPoint()
}
