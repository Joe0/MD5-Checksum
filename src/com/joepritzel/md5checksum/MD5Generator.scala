package com.joepritzel.md5checksum

import java.io.File
import java.security.MessageDigest
import java.io.FileInputStream
import java.security.DigestInputStream
import java.util.Properties
import java.io.FileOutputStream

/**
  * Recursively goes through the 'files' folder and getting the checksum of each file.<br>
  * Then, stores it in a file called MD5CHECKSUM.
  *
  * @author Joe Pritzel
  */
object MD5Generator {

	def main(args : Array[String]) : Unit = {
		
		// The 'root' directory.
		val dir = new File("files")

		val properties = new Properties
		
		getFiles(dir) filterNot (_ isDirectory) foreach {
			f =>
				properties.setProperty(f.getPath().drop(dir.getName.length + 1), checksum(f, "MD5"))
		}
		
		val fos = new FileOutputStream("MD5CHECKSUM")
		properties.store(fos, null)
		fos.close
	}

	/**
	  * Returns the filesystem with the root as the initial file.
	  */
	private def getFiles(f : File) : Stream[File] = {
		f #:: (if (f isDirectory) f.listFiles.toStream.flatMap(getFiles) else Stream.empty)
	}

	/**
	  * Generates the MD5 checksum of the given file.
	  */
	private def checksum(f : File, algo : String) = {
		val md = MessageDigest.getInstance(algo)
		val dis = new DigestInputStream(new FileInputStream(f), md)
		dis.read(new Array[Byte](dis.available), 0, dis.available)
		dis.close
		md.digest.map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
	}

}
