import java.util.List;

public class Environnement implements Runnable {
	protected  State [][] L; 
	protected Thread t;
	protected Game g;

	public Environnement(Game g) {
		this.g = g;
		t =new Thread(this);
		L = new State[5][5];
		for (int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				L[i][j] = State.empty;
			}
		}
		t.start();
	}


	public void run() {
		while(g.isRunning) {
			try { 
				Thread.sleep(650);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			double r = Math.random(); 
			if (r <= 0.5) { 
				int a = (int)(Math.random()*5+1) - 1; 
				int b = (int)(Math.random()*5+1) - 1;
				if (L[a][b]==State.empty) L[a][b] = State.dust; 
				else if (L[a][b]==State.jewelry) L[a][b] = State.dustjewelry; 
			}
			if (r>=0.3 && r<=0.65) {
				int a = (int)(Math.random()*5+1) - 1;
				int b = (int)(Math.random()*5+1) - 1;
				if (L[a][b]==State.empty) L[a][b] = State.jewelry;
				else if (L[a][b]==State.dust) L[a][b] = State.dustjewelry;
			}

		}
	}

}
