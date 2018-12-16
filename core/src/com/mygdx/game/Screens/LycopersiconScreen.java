package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Background;
import com.mygdx.game.Lycopersicon;
import com.mygdx.game.TomatoCluster;
import com.mygdx.game.TomatoWorld;


public class LycopersiconScreen implements Screen{
    final Lycopersicon game;

    private ScreenViewport tViewport;
    private SpriteBatch tBatch;
    private BitmapFont tFont;
    private FreeTypeFontGenerator tGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter tParams;
    private GlyphLayout tLayout;

    private TomatoWorld tWorld;
    private TomatoCluster tCluster;
    private Background tBackground;

    private int tStemNumber;
    private float tTileSize;

    public LycopersiconScreen(Lycopersicon game) {
        this.game = game;
        tViewport = new ScreenViewport();

        tBatch = new SpriteBatch();


       // System.out.println(tViewport.getScreenWidth());

        tStemNumber = MathUtils.random(4);



       // this works fine...  tBackground= new Background(tViewport,tViewport.getScreenWidth()/10);


       // System.out.println(tTileSize);

        Gdx.input.setInputProcessor(tWorld);
    }

    @Override
    public void show() {
        tWorld = new TomatoWorld(tViewport, tBatch, tTileSize); // 800 x 480 world

        System.out.println(tViewport.getScreenWidth());
        tTileSize = tViewport.getScreenWidth()/10;

        tGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Cabin_Sketch/CabinSketch-Regular.ttf"));
        tParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tLayout = new GlyphLayout();

        tParams.size = (int) (tViewport.getScreenHeight()/7);

        tParams.color = Color.BLACK;
        tFont = tGenerator.generateFont(tParams);

        tBackground= new Background(tViewport,tTileSize); //this must be buggy

        System.out.println(tTileSize);

        tCluster = new TomatoCluster(5,4,tStemNumber, tViewport,true, tTileSize);
        tCluster.setDebug(true);
        tCluster.setPosition(0, 0);
        tCluster.fill();

        tWorld.addActor(tBackground);
        tWorld.addActor(tCluster);


    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tWorld.draw();
        tWorld.act(delta);
        drawHUD();
    }

    private Drawable textureToDrawable(Texture t) // I made this method to convert textures to drawables for ease of modification in the table
    {
        return new TextureRegionDrawable(new TextureRegion(t));
    }

    @Override
    public void resize(int width, int height) {
        tViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        tBatch.dispose();
    }
    private void drawHUD()
    {
        tBatch.begin();
        tLayout.setText(tFont, "STEMS:" + tStemNumber);
        tFont.draw(tBatch, tLayout, 0, tViewport.getScreenHeight());
        tBatch.end();
    }



}


