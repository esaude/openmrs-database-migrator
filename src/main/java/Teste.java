import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Teste {
  private static String JOB_FOLDER = "pdiresources/jobs";

  public static void main(String[] args) throws IOException {
    System.out.println("File:  copied to " + JOB_FOLDER);
    Files.list(Paths.get(JOB_FOLDER))
        .forEach(
            job -> {
              try {

                System.out.println(
                    "File:" + job.getFileName().toString() + " copied to " + JOB_FOLDER);
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
  }
}
