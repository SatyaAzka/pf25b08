import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

class SoundSettings {
   public static enum Volume {
      MUTE, LOW, MEDIUM, HIGH
   }

   public static SoundSettings.Volume volume = SoundSettings.Volume.LOW;
}

public enum SoundEffect {
   PLAY("audio/play.wav"),
   DRAW("audio/draw.wav"),
   WIN("audio/win.wav"),
   LOSE("audio/lose.wav"),
   BG2P("audio/2Player.wav"),
   BGEasy("audio/EasySFX.wav"),
   BGHard("audio/HardSFX.wav");

   private Clip clip;

   private SoundEffect(String soundFileName) {
      try {
         URL url = this.getClass().getClassLoader().getResource(soundFileName);
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
         clip = AudioSystem.getClip();
         clip.open(audioInputStream);
         applyVolume();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void play() {
      if (SoundSettings.volume != SoundSettings.Volume.MUTE) {
         if (clip.isRunning()) clip.stop();
         clip.setFramePosition(0);
         clip.start();
      }
   }

   public void loop() {
      if (SoundSettings.volume != SoundSettings.Volume.MUTE) {
         if (clip.isRunning()) clip.stop();
         clip.setFramePosition(0);
         clip.loop(Clip.LOOP_CONTINUOUSLY);
      }
   }

   public void stop() {
      if (clip != null && clip.isRunning()) clip.stop();
   }

   public static void setGlobalVolume(SoundSettings.Volume newVolume) {
      SoundSettings.volume = newVolume;
      for (SoundEffect sfx : values()) {
         sfx.applyVolume();
      }
   }

   public void applyVolume() {
      if (clip != null && SoundSettings.volume != null) {
         FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
         float volumeValue;
         switch (SoundSettings.volume) {
            case MUTE -> volumeValue = -80.0f;
            case LOW -> volumeValue = -20.0f;
            case MEDIUM -> volumeValue = -10.0f;
            case HIGH -> volumeValue = 0.0f;
            default -> volumeValue = -10.0f;
         }
         gainControl.setValue(volumeValue);
      }
   }

   public static void initGame() {
      values();
   }
}