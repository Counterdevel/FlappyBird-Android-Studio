package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {

	//C O N S T R U Ç Ã O
	private SpriteBatch batch;																		//Variavel para textura
	private Texture passaro;																		//Variavel de textura para o passaro
	private Texture fundo;																			//Variavel de textura para o fundo

	//M O V I M E N T A
	private int movimentaY = 0;																		//Variavel para movimento no eixo Y
	private int movimentaX = 0;																		//Variavel para movimento no eixo X

	//T E L A
	private float larguraDis;																		//Variavel da largura do dispositivo
	private float alturaDis;																		//Variavel da altura do dispositivo
	
	@Override
	public void create () {

		batch = new SpriteBatch();																	//construção do batch
		passaro = new Texture("passaro1.png");											//Aplica textura para o passaro
		fundo = new Texture("fundo.png");												//Aplica textura para o fundo

		larguraDis = Gdx.graphics.getWidth();														//Pega a largura do dispositivo
		alturaDis = Gdx.graphics.getHeight();														//Pega a altura do dispositivo

	}

	@Override
	public void render () {

		batch.begin();																				//Inicializa a renderização

		batch.draw(fundo,0,0, larguraDis, alturaDis);										//Renderiza o conteudo
		batch.draw(passaro, 50,50, movimentaX, movimentaY);									//Renderiza o passaro

		movimentaX++;
		movimentaY++;

		batch.end();																				//Termina a renderização

	}
	
	@Override
	public void dispose () {

	}
}
