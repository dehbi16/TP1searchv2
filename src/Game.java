import java.util.concurrent.Semaphore;

public class Game {
	protected Agent agent;
	protected Environnement env;
	protected boolean isRunning;
	protected Manoir manoir;
	protected int mode = 1;
	public Game() {
		init();
		while(isRunning) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			afficher();
		}

	}

	private void afficher() {
		// TODO Auto-generated method stub
		for(int i=0; i<5; i++) {
			for (int j=0; j<5; j++) {
				manoir.placementD_J_DJ_R(i, j,env.L[i][j].toString() );
				manoir.miseAjourSurLeManoirInfoAgent(String.valueOf(agent.nbaspirer), 
						String.valueOf(agent.nbbijoux),
						String.valueOf(agent.cout),
						String.valueOf(agent.erreur));
			}
		}
	}

	private void init() {
		isRunning = true;
		manoir= new Manoir();
		env = new Environnement(this);
		
		int a;
		int b;
		for (int i=0; i<5; i++) {
			a = (int)(Math.random()*5+1) - 1;
			b = (int)(Math.random()*5+1) - 1;
			env.L[a][b] = State.dust;

		}

		for (int i=0; i<3; i++) {
			a = (int)(Math.random()*5+1) - 1;
			b = (int)(Math.random()*5+1) - 1;
			if (env.L[a][b] == State.dust) env.L[a][b] = State.dustjewelry;
			else env.L[a][b] = State.jewelry;
		}
		a = (int)(Math.random()*5+1) - 1;
		b = (int)(Math.random()*5+1) - 1;

		env.L[a][b] = State.robot;
		agent = new Agent(b, a, this);
		manoir.placementD_J_DJ_R(a, b, "4");
	}


}
