package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class Game extends ApplicationAdapter {

	private int pontos = 0;																			//variavel para os pontos
	private int gravidade = 0;																		//Variavel para a gravidade

	//T E X T U R A S
	private SpriteBatch batch;																		//Variavel para textura
	private Texture[] passaros;																		//Array de textura para o passaro
	private Texture fundo;																			//Variavel de textura para o fundo
	private Texture canoBaixo;																		//Variavel para o cano de baixo
	private Texture canoTopo;																		//Variavel para o cano de cima

	//M O V I M E N T A
	private int movimentaY = 0;																		//Variavel para movimento no eixo Y
	private int movimentaX = 0;																		//Variavel para movimento no eixo X
	private float variacao = 0;																		//Variavel para a variação do passaro

	private float posicaoInicialVerticalPassaro = 0;												//Variavel para posição vertical do passaro
	private float posicaoCanoVertical;																//Variavel da posição vertical do cano
	private float posicaoCanoHorizontal;															//Variavel da posição horizontal do cano
	private float espacoEntreCanos;																	//Variavel do espaço entre os canos

	//T E L A
	private float larguraDis;																		//Variavel da largura do dispositivo
	private float alturaDis;																		//Variavel da altura do dispositivo

	private Random random;																			//Variavel com valor aleatorio
	BitmapFont textoPontuacao;																		//Variavel para o texto de pontuação
	private boolean passouCano = false;																//Variavel para saber se passaro passou ou não pelo cano

	//C O L I S Ã O
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;															//Variaceis para colisão
	private Rectangle retanguloCanoBaixo;
	
	@Override
	public void create () {

		inicializaTexturas();
		inicializaObjetos();

	}

	private void inicializaObjetos() {

		batch = new SpriteBatch();																	//construção do batch
		larguraDis = Gdx.graphics.getWidth();														//Pega a largura do dispositivo
		alturaDis = Gdx.graphics.getHeight();														//Pega a altura do dispositivo
		posicaoInicialVerticalPassaro = alturaDis / 2;												//Pega a altura do dispositivo e divide por 2 para a posição do passaro
		posicaoCanoHorizontal =larguraDis;															//Passa a largura do disposito pata a variavel posicaoCanoHorizontal
		espacoEntreCanos = 350;																		//Espaçamento entre os canos

		textoPontuacao = new BitmapFont();															//Faz a referência ao BitmapFont
		textoPontuacao.setColor(Color.WHITE);														//Muda a cor para branco
		textoPontuacao.getData().setScale(10);														//Muda a escala para 10
	}

	private void inicializaTexturas() {

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");										//Aplica textura para o passaro e cria uma animação
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");												//Aplica textura para o fundo
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

	}

	@Override
	public void render () {

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisao();

	}

	private void detectarColisao() {

		circuloPassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);		//Aplica o colider no passaro

		retanguloCanoCima.set(posicaoCanoHorizontal, alturaDis / 2 - canoTopo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoTopo.getWidth(), canoTopo.getHeight()); //Aplica colider no cano de cima
		retanguloCanoBaixo.set(posicaoCanoHorizontal, alturaDis / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight()); //Aplica colider no cano do topo

		boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);			//Variavel para verificar as colisões com o cano de baixo
		boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);		//Variavel para verificar as colisões com o cano de cima

		if(colisaoCanoBaixo || colisaoCanoCima){
			Gdx.app.log("Log", "Bateu");
		}
	}

	private void desenharTexturas() {
		batch.begin();																				                                                //Inicializa a renderização

		batch.draw(fundo,0,0, larguraDis, alturaDis);										                                              //Renderiza o conteudo
		batch.draw(passaros[(int) variacao], 50,posicaoInicialVerticalPassaro);				                                             	 //Renderiza o passaro aplica a animação e moviemnta o passaro para frente
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDis /2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);		//Renderiza o cano de baixo nas proporções ideais para a tela
		batch.draw(canoTopo, posicaoCanoHorizontal, alturaDis / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);		                       //Renderiza o cano do topo nas porporções ideais para a tela
		textoPontuacao.draw(batch,String.valueOf(pontos), larguraDis /2, alturaDis - 100);											  //Renderiza os pontos na parte superior da tela

		batch.end();																														//Termina a renderização
	}

	private void validarPontos() {

		if(posicaoCanoHorizontal < 50 - passaros[0].getWidth()){									//Pega as informações de posição do passaro e do cano para saber se passou ou não, para poder acrescenter 1 ponto
			if(!passouCano){
				pontos++;
				passouCano = true;
			}
		}

	}

	private void verificarEstadoJogo() {

		posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;									//Movimenta o cano de forma mais lenta
		if(posicaoCanoHorizontal <- canoBaixo.getWidth()){											//Se menor, a posicção do cano horizontal vai ser igual a largura do dispositivo
			posicaoCanoHorizontal = larguraDis;
			posicaoCanoVertical = random.nextInt(400) - 200;
			passouCano = false;
		}

		boolean toqueTela = Gdx.input.justTouched();												//Variavel para reconhecer o toque na tela
		if(Gdx.input.justTouched()){
			gravidade = -25;																		//Aplica valor a gravidade aopós o toque;
		}

		if(posicaoInicialVerticalPassaro > 0 || toqueTela)											//Se a posicao inicial vertical do passaro for maior que 0 e tocarmos na tela
			posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;				//Aplicamos gravidade no passaro

		variacao += Gdx.graphics.getDeltaTime() * 10;

		if(variacao > 3)
			variacao = 0;

		gravidade++;																				//Adiciona +1 para gravidade
	}

	@Override
	public void dispose () {

	}
}
