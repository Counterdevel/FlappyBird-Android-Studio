package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {

	//C O N S T R U Ç Ã O
	private SpriteBatch batch;																		//Variavel para textura
	private Texture[] passaros;																		//Array de textura para o passaro
	private Texture fundo;																			//Variavel de textura para o fundo

	//M O V I M E N T A
	private int movimentaY = 0;																		//Variavel para movimento no eixo Y
	private int movimentaX = 0;																		//Variavel para movimento no eixo X
	private float variacao = 0;																		//Variavel para a variação do passaro
	private float gravidade = 0;																	//Variavel para a gravidade
	private float posicaoInicialVerticalPassaro = 0;												////Variavel para posição vertical do passaro

	//T E L A
	private float larguraDis;																		//Variavel da largura do dispositivo
	private float alturaDis;																		//Variavel da altura do dispositivo
	
	@Override
	public void create () {

		batch = new SpriteBatch();																	//construção do batch

		TexturasAnimadas();

		larguraDis = Gdx.graphics.getWidth();														//Pega a largura do dispositivo
		alturaDis = Gdx.graphics.getHeight();														//Pega a altura do dispositivo
		posicaoInicialVerticalPassaro = alturaDis / 2;

	}

	private void TexturasAnimadas() {
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");										//Aplica textura para o passaro e cria uma animação
		passaros[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");												//Aplica textura para o fundo
	}

	@Override
	public void render () {

		batch.begin();																				//Inicializa a renderização

		MovimentoComGravidade();

		batch.draw(fundo,0,0, larguraDis, alturaDis);										//Renderiza o conteudo
		batch.draw(passaros[(int) variacao], movimentaX,posicaoInicialVerticalPassaro);				//Renderiza o passaro aplica a animação e moviemnta o passaro para frente

		variacao += Gdx.graphics.getDeltaTime() * 10;

		gravidade++;																				//Adiciona +1 para gravidade
		movimentaX++;																				//Adiciona +1 para moviemntaX
		movimentaY++;																				//Adiciona +1 para movimentaY

		batch.end();																				//Termina a renderização

	}

	private void MovimentoComGravidade() {
		if(variacao > 3)
			variacao = 0;

		boolean toqueTela = Gdx.input.justTouched();												//Variavel para reconhecer o toque na tela
		if(Gdx.input.justTouched()){
			gravidade = -25;																		//Aplica valor a gravidade aopós o toque;
		}

		if(posicaoInicialVerticalPassaro > 0 || toqueTela)											//Se a posicao inicial vertical do passaro for maior que 0 e tocarmos na tela
			posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;				//Aplicamos gravidade no passaro
	}

	@Override
	public void dispose () {

	}
}
