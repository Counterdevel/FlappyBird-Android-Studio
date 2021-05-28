package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class Game extends ApplicationAdapter {

	private int pontuacaoMaxima = 0;
	private int pontos = 0;																			//variavel para os pontos
	private int gravidade = 0;																		//Variavel para a gravidade
	private int estadoJogo = 0;																		//Variavel para verificar o estado do jogo

	//T E X T U R A S
	private Texture[] passaros;																		//Array de textura para o passaro
	private Texture fundo;																			//Variavel de textura para o fundo
	private Texture canoBaixo;																		//Variavel para o cano de baixo
	private Texture canoTopo;																		//Variavel para o cano de cima
	private Texture gameOver;

	//P O S I Ç Ã O
	private float larguraDis;																		//Variavel da largura do dispositivo
	private float alturaDis;																		//Variavel da altura do dispositivo
	private float posicaoInicialVerticalPassaro = 0;												//Variavel para posição vertical do passaro
	private float variacao = 0;																		//Variavel para a variação de imagens do passaro
	private float posicaoHorizontalPassaro = 0;														//Variavel para passar valor da posição horizontal do passaro
	private float posicaoCanoHorizontal;															//Variavel da posição horizontal do cano
	private float posicaoCanoVertical;																//Variavel da posição vertical do cano
	private float espacoEntreCanos;																	//Variavel do espaço entre os canos

	//T E X T O
	private SpriteBatch batch;																		//Variavel para textura
	BitmapFont textoPontuacao;																		//Variavel para o texto de pontuação
	BitmapFont textoReiniciar;																		//Variavel para o texto para reiniciar
	BitmapFont textoMelhorPontuacao;																//Variavel para o texto da melhor pontuação

	Sound somColisao;
	Sound somVoar;
	Sound somPontos;

	private Random random;																			//Variavel com valor aleatorio
	private boolean passouCano = false;																//Variavel para saber se passaro passou ou não pelo cano

	//C O L I S Ã O
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;															//Variaveis para colisão
	private Rectangle retanguloCanoBaixo;

	Preferences preferencias;
	
	@Override
	public void create () {
		inicializaTexturas();
		inicializaObjetos();
	}

	@Override
	public void render () {
		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisao();
	}

	private void inicializaTexturas() {

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");										//Aplica textura para o passaro e cria uma animação
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");									//Aplica as texturas
		gameOver = new Texture("game_over.png");

	}

	private void inicializaObjetos() {

		batch = new SpriteBatch();																	//construção do batch

		random = new Random();																		//Varivel pega propriedades do Random
		larguraDis = Gdx.graphics.getWidth();														//Pega a largura do dispositivo
		alturaDis = Gdx.graphics.getHeight();														//Pega a altura do dispositivo
		posicaoInicialVerticalPassaro = alturaDis / 2;												//Pega a altura do dispositivo e divide por 2 para a posição do passaro
		posicaoCanoHorizontal = larguraDis;															//Passa a largura do disposito pata a variavel posicaoCanoHorizontal
		espacoEntreCanos = 400;																		//Espaçamento entre os canos

		textoPontuacao = new BitmapFont();															//Faz a referência ao BitmapFont
		textoPontuacao.setColor(Color.WHITE);														//Muda a cor para branco
		textoPontuacao.getData().setScale(10);														//Muda a escala para 10

		textoMelhorPontuacao = new BitmapFont();													//Faz a referência ao BitmapFont
		textoMelhorPontuacao.setColor(Color.RED);													//Muda a cor para vermelho
		textoMelhorPontuacao.getData().setScale(2);													//Muda a escala para 10

		textoReiniciar = new BitmapFont();															//Faz a referência ao BitmapFont
		textoReiniciar.setColor(Color.GREEN);														//Muda a cor para verde
		textoReiniciar.getData().setScale(2);														//Muda a escala para 10

		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoCima = new Rectangle();														//Pega as referências de cada classe
		retanguloCanoBaixo = new Rectangle();

		somVoar = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));				//Adiciona os sons
		somPontos = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);				//Pega as informações necessárias para armazenar

	}

	private void verificarEstadoJogo() {

		boolean toqueTela = Gdx.input.justTouched();												//Variavel para reconhecer o toque na tela

		if (estadoJogo == 0)																		//Se estado for igual a 0, jogo não inicia
		{
			if(Gdx.input.justTouched()){
				gravidade = -20;																	//Aplica valor a gravidade após o toque;
				estadoJogo = 1;																		//Muda estado do jogo para 1
				somVoar.play();																		//Aplica som das asas ao tocar na tela
			}
		} else if(estadoJogo == 1) {																//Se estado for igual a 1, jogo inicia
			if (Gdx.input.justTouched()) {
				gravidade = -20;                                                                    //Aplica valor a gravidade após o toque;
				somVoar.play();																		//Aplica som das asas ao tocar na tela
			}


			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 400;                             //Movimenta o cano de forma mais lenta
			if (posicaoCanoHorizontal < -canoBaixo.getHeight()) {                                   //Se menor, a posicção do cano horizontal vai ser igual a largura do dispositivo
				posicaoCanoHorizontal = larguraDis;													//Passa a largura do dispositivo para a variavel posicaoCanoHorizontal
				posicaoCanoVertical = random.nextInt(600) - 200;									//Randomiza a posção vertical do cano
				passouCano = false;
			}

			if (posicaoInicialVerticalPassaro > 0 || toqueTela)                                     //Se a posicao inicial vertical do passaro for maior que 0 e tocarmos na tela
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;          //Aplicamos gravidade no passaro

			gravidade++;                                                                            //Adiciona +1 para gravidade

		} else if(estadoJogo == 2){																	//Se estado for igual a 2, game over e reseta as informações do jogo

			if(pontos > pontuacaoMaxima){
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);					//Armazena a melhor pontuação
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;

			if(toqueTela) {
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDis / 2;
				posicaoCanoHorizontal = larguraDis;
			}
		}
	}

	private void validarPontos() {

		if(posicaoCanoHorizontal < 50 - passaros[0].getWidth()){									//Pega as informações de posição do passaro e do cano para saber se passou ou não, para poder acrescenter 1 ponto
			if(!passouCano){
				pontos++;
				passouCano = true;
				somPontos.play();																	//Ao passar pelo cano o som de ponto é tocado
			}
		}
		variacao += Gdx.graphics.getDeltaTime() * 10;												//velocidade da animação
		if(variacao > 3)																			//Faz o loop da animação
			variacao = 0;

	}

	private void desenharTexturas() {
		batch.begin();																				                                                //Inicializa a renderização

		batch.draw(fundo,0,0, larguraDis, alturaDis);                                       	//Renderiza o conteudo
		batch.draw(passaros[(int) variacao], 50 + posicaoHorizontalPassaro,
				posicaoInicialVerticalPassaro);													  	//Renderiza o passaro aplica a animação e moviemnta o passaro para frente
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDis /2 - canoBaixo.getHeight()
				- espacoEntreCanos / 2 + posicaoCanoVertical);										//Renderiza o cano de baixo nas proporções ideais para a tela
		batch.draw(canoTopo, posicaoCanoHorizontal, alturaDis / 2 + espacoEntreCanos
				/ 2 + posicaoCanoVertical);		                       								//Renderiza o cano do topo nas porporções ideais para a tela
		textoPontuacao.draw(batch,String.valueOf(pontos), larguraDis /2, alturaDis - 100);	//Renderiza os pontos na parte superior da tela

		if(estadoJogo == 2)
		{
			batch.draw(gameOver, larguraDis / 2 - gameOver.getWidth() /2, alturaDis / 2);	//Renderixa o Game OVer
			textoReiniciar.draw(batch, "TOQUE NA TELA PARA REINICIAR",
					larguraDis / 2 - 245, alturaDis /2 - gameOver.getHeight() / 2);			//Renderiza o texto para reiniciar
			textoMelhorPontuacao.draw(batch, "SUA MELHOR PONTUAÇÃO É: " + pontuacaoMaxima +" Pontos",
					larguraDis /2 - 225, alturaDis /2 - gameOver.getHeight() * 2);			//Renderiza o texto de melhor pontuação
		}

		batch.end();																														//Termina a renderização
	}

	private void detectarColisao() {

		circuloPassaro.set(50 + passaros[0].getWidth() / 2, posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);		//Aplica o colider no passaro

		retanguloCanoCima.set(posicaoCanoHorizontal, alturaDis / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoTopo.getWidth(), canoTopo.getHeight()); //Aplica colider no cano de cima
		retanguloCanoBaixo.set(posicaoCanoHorizontal, alturaDis / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight()); //Aplica colider no cano do topo

		boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);			//Variavel para verificar as colisões com o cano de baixo
		boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);		//Variavel para verificar as colisões com o cano de cima

		if(colisaoCanoBaixo || colisaoCanoCima){
			Gdx.app.log("Log", "Bateu");
			if(estadoJogo == 1) {
				somColisao.play();																	//Toca um som de colisão ao colidir com canos
				estadoJogo = 2;
			}
		}
	}

	@Override
	public void dispose () {

	}
}
