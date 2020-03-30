package proofassistant;


public class Test implements Runnable {

    @Override
    public void run() {
        music();
    }
    
    private void music() {
        try {
            SoundUtils.tone(247, 300);
            while (true && !Globals.reverse2PremIntro) {
                SoundUtils.tone(330, 100);
                SoundUtils.tone(392, 300);
                SoundUtils.tone(494, 100);
                SoundUtils.tone(660, 300);
                SoundUtils.tone(494, 100);
                SoundUtils.tone(392, 300);
                SoundUtils.tone(330, 100);

                SoundUtils.tone(247, 300);
                SoundUtils.tone(330, 100);
                SoundUtils.tone(392, 300);
                SoundUtils.tone(494, 100);
                SoundUtils.tone(660, 300);
                Thread.sleep(200);
                
                SoundUtils.tone(494, 700);
                SoundUtils.tone(587, 100);
                SoundUtils.tone(740, 300);
                SoundUtils.tone(988, 100);
                SoundUtils.tone(1174, 300);
                SoundUtils.tone(988, 100);
                SoundUtils.tone(740, 300);
                SoundUtils.tone(587, 100);
                
                SoundUtils.tone(494, 300);
                SoundUtils.tone(587, 100);
                SoundUtils.tone(740, 300);
                SoundUtils.tone(988, 100);
                SoundUtils.tone(1174, 900);
                
                SoundUtils.tone(1245, 100);
                SoundUtils.tone(1174, 300);
                SoundUtils.tone(1109, 100);
                SoundUtils.tone(587, 400);
                SoundUtils.tone(1174, 400);
                
                SoundUtils.tone(587, 300);
                SoundUtils.tone(622, 100);
                SoundUtils.tone(587, 100);
                SoundUtils.tone(554, 100);
                SoundUtils.tone(554, 100);
                SoundUtils.tone(554, 100);
                SoundUtils.tone(554, 400);
                
                SoundUtils.tone(293, 500);
                SoundUtils.tone(311, 100);
                SoundUtils.tone(293, 300);
                SoundUtils.tone(277, 400);
                SoundUtils.tone(311, 100);
                SoundUtils.tone(293, 300);
                
                SoundUtils.tone(277, 400);
                SoundUtils.tone(392, 100);
                SoundUtils.tone(370, 800);
                SoundUtils.tone(247, 700);
            }
            
        } catch (Exception e) {
            
        }
    }
}