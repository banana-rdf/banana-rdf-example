package org.w3.example

import org.w3.banana._, jena._

import JenaImplicits._

object JenaCollectorTest extends CollectorTest[Jena]

import com.hp.hpl.jena.query.Dataset

object JenaStoreTest extends StoreTest[Jena, Dataset] {

  def rmdir(dir: String): Unit = {
    import java.io._
    import java.nio.file._, attribute._

    val directory = Paths.get(dir)

    if (new File(tempDir).exists) {

      Files.walkFileTree(directory, new SimpleFileVisitor[Path] {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          Files.delete(file)
          FileVisitResult.CONTINUE
        }

        override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
          Files.delete(dir)
          FileVisitResult.CONTINUE
        }

      })

    }

  }

  def makeStore(): Dataset = {

    val tempDir = "tmpGraphStoreDir"

    rmdir(tempDir)

    import com.hp.hpl.jena.tdb.TDBFactory

    TDBFactory.createDataset(tempDir)
  }

}

