package io.silksmith.js.closure

class ClosureUtil {


	static def getOrderedFileInfos(def fileInfos, entryPoint) {
		def provideMap = [:]
		fileInfos.each({ FileInfo info ->
			info.provides.each({ provideMap[it] = info })
			provideMap
		})

		FileInfo rootFileInfo = provideMap[entryPoint]
		def sortedFileInfos = [] as Set

		def addRequired
		addRequired = { require ->

			FileInfo fi = provideMap[require]
			fi.requires.each(addRequired)

			sortedFileInfos << fi
		}
		rootFileInfo.requires.each(addRequired)
		sortedFileInfos << rootFileInfo
		def jsCode = new StringBuilder()

		sortedFileInfos.each { FileInfo fileInfo ->

			jsCode.append(fileInfo.file.text)
			jsCode.append("\n")
		}
	}
}
