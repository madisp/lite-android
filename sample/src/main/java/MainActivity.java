import android.app.Activity;
import android.os.Bundle;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // I can use Path, boo!
    Path path = Paths.get("whatevs");
  }
}
