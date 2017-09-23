package br.com.marcosgois.gamewgdx.gameoflife

import java.awt.Rectangle

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.{ApplicationAdapter, Gdx}

import scala.collection.mutable.Stack

class GameOfLife (var width: Int, var height: Int) extends ApplicationAdapter {

  var dropImage = new Texture(Gdx.files.internal("droplet.png"))      //Instanciando iamgem da gota
  var bucketImage = new Texture(Gdx.files.internal("bucket.png"))     //Instanciando imagem do balde
  var dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))  //Instanciando som da gota [soa ao tocar no balde]
  var rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))  //Instanciando música de fundo [chuva]
  var bucket = new Rectangle()                                        //Instanciando um retangulo que será o balde [Imagem do balde]

  var camera = new OrthographicCamera()                               //Instanciando a câmera
  var batch = new SpriteBatch()                                       //TODO: PESQUISAR O QUE É "BATCH"!

  val rainsDrops = Stack[Rectangle]()                                 //Pilha com as gotas à caírem
  val lastDropTime = TimeUtils.nanoTime()                             //Instanciando variável TODO: BUSCAR QUAL TEMPO ESSA FUNÇÃO RETORNA!

  def spawnRainDrop() : Unit ={                                       //Função que controla as quedas das gotas
    val rainDrop = new Rectangle()                                    //Instanciando um retangulo que será a gota [Imagem da gota]
    rainDrop.x = MathUtils.random(0, (width - 64))                    //Seta a posição de eixo X para um número randômico entre 0 e a largura que será renderizada
    rainDrop.y = height                                               //Seta aonde a gota começará, ou seja, na maior altura possível da tela que será renderizada [o prórpio parâmetro de altura (height)]
    rainDrop.width = 64                                               //Seta o tamanho[largura] do retângulo que será a gota
    rainDrop.height = 64                                              //Seta o tamanho[altura] do retângulo que será a gota
    rainsDrops.push(rainDrop)                                         //Acrescenta em uma pilha a gota com suas propriedades setadas
  }

  override def create(): Unit = {                                     //Ao ser renderizada a página .. TODO: PESQUISAR O QUE ESSA FUNÇÃO FAZ!
    rainMusic.setLooping(true)                                        //Seta música em estado de looping [repetição]
    rainMusic.play()                                                  //A música começa a tocar
    camera.setToOrtho(false, width, height)                           //TODO: PESQUISAR O QUE ESSA FUNÇÃO FAZ!
    bucket.x = ((width/2) - (64/2))                                   //Seta a propiedade do balde, no eixo X [Começa no meio da tela]
    bucket.y = 20                                                     //Seta a propriedade do bolde, no eixo Y, a altura
    bucket.width = 64                                                 //Seta o tamanho [largura] do balde
    bucket.height = 64                                                //Seta o tamanho [altura] do balde

    spawnRainDrop()                                                   //Cria a primeira gota
  }

  override def render(): Unit = {                                     //Renderização
    Gdx.gl.glClearColor(0, 0, 0.2f, 1)                                //FLOAT RED[0], GREEN[0], BLUE[0.2f], ALPHA[1] (Limpa a tela com um azul escuro)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)                          //Limpa os bits TODO: PESQUISAR O QUE ESSA FUNÇÃO FAZ

    camera.update()                                                   //Camera chama atualização das matrizes
    batch.setProjectionMatrix(camera.combined)                        //SprintBatch renderiza nas coordenadas especificadas pela câmera
    batch.begin()                                                     //Inicia um novo batch
    batch.draw(dropImage, bucket.x, bucket.y)                         //Desenha primeira gota na tela nas coordenadas do balde
    for (raindrop <- rainsDrops){                                     //Percorre a pilha de gotas
      batch.draw(dropImage, raindrop.x, raindrop.y)                   //Desenha uma gota nas coordenadas da gota lida na pilha
      //rainsDrops.pop()                                                //Remove a gota da pilha TODO: TRATAR EXCEÇÃO CASO NÃO POSSUA NADA NA PILHA
    }
    batch.end()                                                       //Encerra o BATCH

    if(Gdx.input.isKeyPressed(Keys.LEFT)) {
      bucket.x = (bucket.x - 200) * Gdx.graphics.getDeltaTime().toInt //Lê a tecla LEFT do teclado e processa-a
    }
    if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
      bucket.x = (bucket.x + 200) * Gdx.graphics.getDeltaTime().toInt //Lê a tecla RIGHT do teclado e precessa-a
    }
    if(bucket.x < 0) bucket.x = 0                                     //Caso as coordenadas do balde saiam da janela renderizada [pela esquerda], mantém o balde dentro
    if(bucket.x > width - 64) bucket.x = width - 64                   //Caso as coordenadas do balde saiam da janela renderizada [pela direita], mantém o balde dentro
    if(TimeUtils.nanoTime() - lastDropTime > 1000000000) {
      spawnRainDrop()                                                 //Caso o tempo da ultima gota - o tempo "nanoTime" seja maior que 1.000.000.000 desenhe uma nova gota
    }

    while(rainsDrops.length > 0){                                     //Enquanto a lista tiver elementos
      val raindrop = rainsDrops.pop()                                 //POP elemento da lista
      rainsDrops.push(raindrop)                                       //PUSH elemento de volta pra lista
      raindrop.y = (raindrop.y - 200) * Gdx.graphics.getDeltaTime().toInt //Gota cai
      if(raindrop.y + 64 < 0) {rainsDrops.pop()}                      //Caso chegue ao chão, suma com a gota
      if(raindrop.y > bucket.y && raindrop.y < (bucket.y + 64) && raindrop.x == bucket.x){  //Caso encontre o balde
        dropSound.play()                                              //Som da gota sendo captada pelo balde
      }
    }
  }

  override def dispose(): Unit = {
    dropImage.dispose()                                               // dispose of all the native resources
    bucketImage.dispose()                                             // dispose of all the native resources
    dropSound.dispose()                                               // dispose of all the native resources
    rainMusic.dispose()                                               // dispose of all the native resources
    batch.dispose()                                                   // dispose of all the native resources
  }

}