package cr.ac.itcr.andreifuentes.flappybirdclase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	Texture background;
	Texture topTube;
	Texture bottomTube;
	Texture[] birds;
	Texture birds_down;
	Texture bird_descending;
	Texture gameOver;
	Texture easy;
	Texture difficult;

	int birdState;
	float gap;
	float birdY;
	float velocity;
	float gravity;
	int numberOfPipes = 4;
	float pipeX[] = new float[numberOfPipes];
	float pipeYOffset[] = new float[numberOfPipes];
	float distance;
	float pipeVelocity = 5;
	Random random;
	float maxLine;
	float minLine;
	int score;
	int pipeActivo;
	BitmapFont font;
	int game_state;
	int birdSize;
	int disminucion_velocidad;

	Circle birdCircle;
	Rectangle[] topPipes;
	Rectangle[] bottomPipes;
	float bajando;
	Music music_fly;
	Music music_die;
	Music music_score;
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		background = new Texture("bg.png");
		birds = new Texture[2];

		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		birds_down = new Texture("bird_down.png");

		bird_descending = new Texture("bird_descending.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameOver = new Texture("gameOverOriginal.png");
		easy = new Texture ("easy.png");
		difficult = new Texture("difficult.png");

		birdCircle = new Circle();
		topPipes = new Rectangle[numberOfPipes];
		bottomPipes = new Rectangle[numberOfPipes];

		birdState = 0;
		game_state = 0;
		gap = 500;
		velocity = 0;
		gravity = 0.7f;
		random = new Random();
		distance = Gdx.graphics.getWidth() * 3/5;
		maxLine = Gdx.graphics.getHeight()* 3/4;
		minLine = Gdx.graphics.getHeight()* 1/4;
		score = 0;
		pipeActivo = 0;
		birdSize = 0;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		music_fly = Gdx.audio.newMusic(Gdx.files.internal("flap.wav"));
		//music.setLooping(true);
		music_fly.setVolume(0.4f);

		music_die = Gdx.audio.newMusic(Gdx.files.internal("hurt.wav"));
		//music.setLooping(true);
		music_die.setVolume(0.4f);

		music_score = Gdx.audio.newMusic(Gdx.files.internal("score.wav"));
		//music.setLooping(true);
		music_score.setVolume(0.4f);


		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[birdState].getHeight()/2;

		bajando = birdY;
		for (int i = 0; i<numberOfPipes; i++){
			pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
			pipeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth() + Gdx.graphics.getWidth() + distance*i;

			// inicializamos cada uno de los Shapes
			topPipes[i] = new Rectangle();
			bottomPipes[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		// no iniciado
		if (game_state == 0){
			batch.draw(easy, Gdx.graphics.getWidth()/2 - easy.getWidth()/2, (Gdx.graphics.getHeight()/2 - easy.getHeight()/2)+350);

			batch.draw(difficult, Gdx.graphics.getWidth()/2 - difficult.getWidth()/2, (Gdx.graphics.getHeight()/2 - difficult.getHeight()/2)-350);

			if (Gdx.input.justTouched()) {
				if (Gdx.input.getY() < Gdx.graphics.getHeight() / 2){
					gap = 530;
					velocity = 0;
					disminucion_velocidad = 20;
					distance = Gdx.graphics.getWidth() * 3/5;

				}
				else{
					gap = 365;
					velocity = 5;
					disminucion_velocidad = 20;
					distance = Gdx.graphics.getWidth() * 3/6;
				}

				game_state = 1;
			}
		}
		// jugando
		else if (game_state == 1){
			if (pipeX[pipeActivo] < Gdx.graphics.getWidth()/2 - topTube.getWidth()){
				music_score.play();
				score++;

				if (pipeActivo < numberOfPipes - 1){
					pipeActivo++;
				}
				else {
					pipeActivo = 0;
				}

				Gdx.app.log("score", Integer.toString(score));
			}


			birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[birdState].getHeight()/2, birds[birdState].getWidth()/2);

//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.MAGENTA);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//

			// Posicionamiento de los pipes
			for (int i = 0; i<numberOfPipes; i++) {

				if (pipeX[i] < -topTube.getWidth()){
					pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
					pipeX[i] += distance*(numberOfPipes);
				}
				else {
					pipeX[i] = pipeX[i] - pipeVelocity;
				}

				batch.draw(topTube,
						pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				batch.draw(bottomTube,
						pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

				topPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube.getWidth(),
						topTube.getHeight());
				bottomPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]-bottomTube.getHeight()-gap/2,
						bottomTube.getWidth(),
						bottomTube.getHeight());

//				shapeRenderer.rect(topPipes[i].x, topPipes[i].y, topTube.getWidth(),
//						topTube.getHeight());
//				shapeRenderer.rect(bottomPipes[i].x, bottomPipes[i].y, bottomTube.getWidth(),
//						bottomTube.getHeight());

				if (Intersector.overlaps(birdCircle, topPipes[i])){
					Gdx.app.log("Intersector", "top pipe overlap");

					music_die.play();
					game_state = 2;
				}
				else if (Intersector.overlaps(birdCircle, bottomPipes[i])){
					Gdx.app.log("Intersector", "bottom pipe overlap");

					music_die.play();
					game_state = 2;
				}
			}

			if (Gdx.input.justTouched()){
				velocity = velocity - disminucion_velocidad;
				music_fly.play();



			}

			birdState = birdState == 0 ? 1 : 0;



			velocity = velocity + gravity;
			bajando = birdY + velocity;


			if (birdY < 0){
				game_state = 2;
				music_die.play();
			}
			else {
				birdY = birdY - velocity;
			}

//			shapeRenderer.end();


		}
		// game over
		else if (game_state == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			batch.draw(birds_down, Gdx.graphics.getWidth() / 2 - birds_down.getWidth()/2,  birdY,  birds_down.getWidth(), birds_down.getHeight());

			if (Gdx.input.isTouched()){

				game_state = 0;
				score = 0;
				pipeActivo = 0;
				velocity = 0;
				startGame();


			}

		}
		if((bajando<birdY && game_state != 2)|| game_state ==0)
			batch.draw(birds[birdState], Gdx.graphics.getWidth() / 2 - birds[birdState].getWidth()/2,  birdY,  birds[birdState].getWidth(), birds[birdState].getHeight());
		else if(bajando>birdY && game_state != 2)
			batch.draw(bird_descending, Gdx.graphics.getWidth() / 2 - bird_descending.getWidth()/2,  birdY,  bird_descending.getWidth(), bird_descending.getHeight());

		font.draw(batch, Integer.toString(score), Gdx.graphics.getWidth()*1/8, Gdx.graphics.getHeight()*9/10);
		//birdSize += 1;
		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
