package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Background;
import com.mygdx.game.CreditsPane;
import com.mygdx.game.CreditsUI;
import com.mygdx.game.GameOverUI;
import com.mygdx.game.LycoButton;
import com.mygdx.game.Lycopersicon;
import com.mygdx.game.LycopersiconTitleUI;
import com.mygdx.game.NextLevelUI;
import com.mygdx.game.ScorePane;
import com.mygdx.game.TapPrompt;
import com.mygdx.game.TomatoCluster;
import com.mygdx.game.TomatoWorld;
import com.mygdx.game.TutorialPage;
import com.mygdx.game.TutorialUI;
import com.mygdx.game.Twinkle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;



public class LycopersiconScreen implements Screen {

    private ScreenViewport tViewport;
    private SpriteBatch tBatch;
    private BitmapFont tFont;
    private FreeTypeFontGenerator tGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter tParams;
    private GlyphLayout tLayout;

    private TomatoWorld tWorld;
    private LycopersiconTitleUI tTitleUI;
    private GameOverUI tGameOverUI;
    private NextLevelUI tNextLevelUI;
    private CreditsUI tCreditsUI;
    private TutorialUI tTutorialUI;

    private TomatoCluster tCluster;
    private Background tBackground;
    private TutorialPage tTutorialPage;


    private TapPrompt tTapPrompt;
    private Image tNextLevel;
    private LycoButton tReplayButton, tHomeButton, tCreditsButton, tBackButton, tTutorialButton;
    private ScorePane tScorePane;
    private CreditsPane tCreditsPane;

    private int tLevel;
    private float tTileSize, tTimeLeft;

    private Preferences tData;
    private Sound tSound, tNextLevelSound;
    private Music tMusic;

    private Lycopersicon tGame;

    private final Pool<Twinkle> twinklePool = new Pool<Twinkle>() {
        @Override
        protected Twinkle newObject() {
            return new Twinkle(tViewport, tViewport.getScreenWidth(), tViewport.getScreenHeight());
        }
    };


    public LycopersiconScreen(Lycopersicon tGame) {
        this.tGame = tGame;

        tLevel = 1;
        tViewport = new ScreenViewport();

        tBatch = new SpriteBatch();


        tTapPrompt = new TapPrompt(tViewport);
        tNextLevel = new Image(new Texture(Gdx.files.internal("nextLevel.png")));

        tReplayButton = new LycoButton(tViewport, new Texture(Gdx.files.internal("replayButton.png")));
        tHomeButton = new LycoButton(tViewport, new Texture(Gdx.files.internal("homeButton.png")));
        tBackButton = new LycoButton(tViewport, new Texture(Gdx.files.internal("homeButton.png")));
        tCreditsButton = new LycoButton(tViewport, new Texture(Gdx.files.internal("creditsButton.png")));
        tTutorialButton = new LycoButton(tViewport, new Texture(Gdx.files.internal("tutorialButton.png")));
        tTutorialPage = new TutorialPage(tViewport);

        tScorePane = new ScorePane(tViewport);
        tCreditsPane = new CreditsPane(tViewport);

        tTimeLeft = 10f;


        tData = Gdx.app.getPreferences("LycopersiconData");
        tSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/pop.mp3"));
        tNextLevelSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/twinkle.mp3"));
        tMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/music.mp3"));




    }

    @Override
    public void show() {
        tMusic.setLooping(true);
        tMusic.play();
        tWorld = new TomatoWorld(tViewport, tBatch); // 800 x 480 world

        tTitleUI = new LycopersiconTitleUI(tViewport);
        tGameOverUI = new GameOverUI(tViewport);
        tNextLevelUI = new NextLevelUI(tViewport);
        tCreditsUI = new CreditsUI(tViewport);
        tTutorialUI = new TutorialUI(tViewport);


        tGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Cabin_Sketch/CabinSketch-Regular.ttf"));

        tParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tLayout = new GlyphLayout();

        tParams.size = (tViewport.getScreenHeight() / 9);

        tParams.color = Color.WHITE;
        tFont = tGenerator.generateFont(tParams);


        tNextLevel.setSize(16 * tViewport.getScreenHeight() / 20, 9 * tViewport.getScreenHeight() / 20);
        tNextLevel.setPosition(-tNextLevel.getWidth(), tViewport.getScreenHeight() / 2 - tNextLevel.getHeight());
        Gdx.input.setInputProcessor(tTitleUI);



    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.getInputProcessor().equals(tTitleUI)) {
            tWorld.draw();
            tTitleUI.draw();
            tTitleUI.act(delta);

        }
        if (Gdx.input.getInputProcessor().equals(tWorld)) {
            if (tCluster.remainingTargets() == 0) {
                nextLevel();
            }
            if (tTimeLeft <= 0) {
                gameOver();
            }

            tTimeLeft -= delta;
            tWorld.draw();
            tWorld.act(delta);
            drawHUD();


        }
        if (Gdx.input.getInputProcessor().equals(tGameOverUI)) {
            tWorld.draw();
            drawHUD();
            tGameOverUI.draw();
            tGameOverUI.act();
        }
        if (Gdx.input.getInputProcessor().equals(tNextLevelUI)) {
            tWorld.draw();
            tWorld.act();
            drawLevel();

        }
        if (Gdx.input.getInputProcessor().equals(tCreditsUI)) {
            tWorld.draw();
            tCreditsUI.draw();
            tCreditsUI.act();
        }
        if (Gdx.input.getInputProcessor().equals(tTutorialUI)) {
            tTutorialUI.draw();
            tTutorialUI.act();
        }


    }

    @Override
    public void resize(int width, int height) {
        //tMusic.play();
        tViewport.update(width, height, true);
        tBackground = new Background(tViewport, tTileSize);
        tTileSize = tViewport.getScreenWidth() / 10;

        tTapPrompt.init();
        tReplayButton.init();
        tHomeButton.init();
        tBackButton.init();
        tCreditsButton.init();
        tTutorialButton.init();
        tScorePane.init();
        tCreditsPane.init();

        tReplayButton.setPosition(tViewport.getScreenWidth() / 2 - tScorePane.getWidth() / 2, tViewport.getScreenHeight());
        tHomeButton.setPosition(tViewport.getScreenWidth() / 2 + tScorePane.getWidth() / 2 - tHomeButton.getWidth(), tViewport.getScreenHeight());
        tCreditsButton.setPosition(tViewport.getScreenWidth() / 50, tViewport.getScreenWidth() / 50);
        tBackButton.setPosition(tViewport.getScreenWidth() / 50, tViewport.getScreenHeight());
        tTutorialButton.setPosition(tViewport.getScreenWidth() - tViewport.getScreenWidth() / 50 - tTutorialButton.getWidth(), tViewport.getScreenWidth() / 50);



        tLevel = 1;

        tCluster = new TomatoCluster(1, tLevel, tViewport, tTileSize);
        //tBackground.initFarm();

        tCluster.fill();

        tTitleUI.addActor(tTapPrompt);
        tTitleUI.addActor(tCreditsButton);
        tTitleUI.addActor(tTutorialButton);

        tGameOverUI.addActor(tReplayButton);
        tGameOverUI.addActor(tHomeButton);
        tGameOverUI.addActor(tScorePane);

        tCreditsUI.addActor(tCreditsPane);
        tCreditsUI.addActor(tBackButton);


        tTutorialUI.addActor(tTutorialPage);

        setUpTutorialUIListener();
        setUpTutorialButtonListener();
        setUpTitleUIListener();
        setUpReplayButtonListener();
        setUpHomeButtonListener();
        setUpCreditsButtonListener();
        setUpBackButtonListener();


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
        tScorePane.dispose();
        tCluster.dispose();
        tCreditsPane.dispose();
        tSound.dispose();
        tNextLevelSound.dispose();
        tMusic.dispose();
        twinklePool.clear();
        tCluster.clearPool();
    }

    private void drawHUD() {
        tBatch.begin();
        tLayout.setText(tFont, "STEMS:" + tCluster.getTarget());
        tFont.draw(tBatch, tLayout, tViewport.getScreenWidth() / 20, tViewport.getScreenHeight() - tViewport.getScreenHeight() / 12 + tViewport.getScreenHeight() / 20);
        tLayout.setText(tFont, "LEFT:" + tCluster.remainingTargets());
        tFont.draw(tBatch, tLayout, tViewport.getScreenWidth() - tViewport.getScreenHeight() / 2, tViewport.getScreenHeight() * 11 / 12 + tViewport.getScreenHeight() / 20);
        tLayout.setText(tFont, "LEVEL:" + tLevel);
        tFont.draw(tBatch, tLayout, tViewport.getScreenWidth() - tViewport.getScreenHeight() / 2, tViewport.getScreenHeight() / 10);
        tLayout.setText(tFont, (int) tTimeLeft + "s");
        tFont.draw(tBatch, tLayout, tViewport.getScreenWidth() / 20, tViewport.getScreenHeight() / 10);
        tBatch.end();
    }


    private void drawLevel() {
        tBatch.begin();
        tLayout.setText(tFont, "LV " + tLevel);
        tFont.draw(tBatch, tLayout, tViewport.getScreenWidth() * 4 / 10, tViewport.getScreenHeight() / 2);
        tLayout.setText(tFont, +(double) ((int) (tTimeLeft * 100)) / 100 + "sec");
        tFont.draw(tBatch, tLayout, tViewport.getScreenWidth() / 20, tViewport.getScreenHeight() / 10);
        tBatch.end();
    }

    private void setUpTitleUIListener() {

        tTitleUI.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.isHandled()) return true;

                tTapPrompt.clearActions();
                /*tTapPrompt.addAction(sequence(parallel(repeat(7, rotateBy(50, .2f)),
                        scaleTo(.5f, .5f, 1.4f)), run(new Runnable() {
                    @Override
                    public void run() {
                        setUpWorld();
                    }
                })));*/
                tTapPrompt.addAction(run(new Runnable() {
                    @Override
                    public void run() {


                        resetGame();
                        Gdx.input.setInputProcessor(tWorld);
                        tMusic.setVolume(.1f);
                    }
                }));
                return true;
            }
        });
    }

    private void setUpTutorialUIListener() {
        tTutorialUI.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.isHandled()) return true;
                if (tTutorialPage.getIndex() == 3) {
                    tTutorialPage.addAction(Actions.sequence(fadeOut(.25f), run(new Runnable() {
                        @Override
                        public void run() {
                            tMusic.setVolume(1.0f);
                            tTutorialPage.reset();
                            Gdx.input.setInputProcessor(tTitleUI);
                        }
                    })));

                } else {
                    tTutorialPage.clearActions();
                    tTutorialPage.display(tTutorialPage.getIndex() + 1);
                }
                return true;
            }
        });
    }

    private void setUpWorldsListener() { //possible future easter egg?
        tWorld.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {

                switch (keycode) {
                    case Input.Keys.M:
                        tCluster.scaleDownVelocity(.7f);
                        break;
                }
                return true;
            }
        });
    }

    private void setUpWorld() {


        Gdx.input.setInputProcessor(tWorld);

    }

    private void setUpTitle() {

        for (int i = 0; i < 50; i++) {
            Twinkle t = new Twinkle(tViewport, tViewport.getScreenWidth(), tViewport.getScreenHeight());

            tTitleUI.addActor(t);
        }
    }


    /**
     * Next level removes the current cluster
     * sets tCluster to a more "difficult" cluster
     */
    private void nextLevel() {
        tMusic.setVolume(1f);

        Gdx.input.setInputProcessor(tNextLevelUI);
        for (Actor t : tWorld.getActors()) {
            if (t instanceof Twinkle) {
                t.addAction(Actions.removeActor());
                twinklePool.free((Twinkle) t);
            }
        }



        tCluster.addAction(Actions.removeActor());
        tCluster.raiseDifficulty();
        tLevel++;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Gdx.input.setInputProcessor(tWorld);
                if (tLevel >= 6) {

                    tBackground.spacetime();

                    for (int i = 0; i < 20; i++) {

                        tWorld.addActor(twinklePool.obtain());
                    }


                }
                tWorld.addActor(tCluster);
                tTimeLeft = 10;
                tMusic.setVolume(.1f);

            }
        }, 3, 0, 0);
    }


    private void resetGame() {
        reset();
        tBackground.remove();
        tCluster.remove();
        tWorld.addActor(tBackground);
        tWorld.addActor(tCluster);
        Gdx.input.setInputProcessor(tWorld);


    }

    private void gameOver() {
        for (Actor t : tWorld.getActors()) {
            if (t instanceof Twinkle) {
                t.addAction(Actions.removeActor());
                twinklePool.free((Twinkle) t);
            }
        }
        tMusic.setVolume(1f);
        updateHighScore();
        tScorePane.addAction(Actions.moveTo(tViewport.getScreenWidth() / 2 - tScorePane.getWidth() / 2, tViewport.getScreenHeight() / 2, .1f));
        tReplayButton.addAction(Actions.moveTo(tViewport.getScreenWidth() / 2 - tScorePane.getWidth() / 2, tViewport.getScreenHeight() / 2 - tScorePane.getHeight() / 2, .1f));
        tHomeButton.addAction(Actions.moveTo(tViewport.getScreenWidth() / 2 + tScorePane.getWidth() / 2 - tHomeButton.getWidth(), tViewport.getScreenHeight() / 2 - tScorePane.getHeight() / 2, .1f));
        tReplayButton.setTouchable(Touchable.enabled);
        tHomeButton.setTouchable(Touchable.enabled);

        Gdx.input.setInputProcessor(tGameOverUI);

    }

    private void updateHighScore() {
        if (tLevel > tData.getInteger("maxLevel")) {
            tData.putInteger("maxLevel", tLevel);
            tData.flush();

        }
        tScorePane.updateHighScore(tData.getInteger("maxLevel"));
        tScorePane.updateScore(tLevel);

    }


    private void setUpReplayButtonListener() {
        tReplayButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                tSound.play();
                tHomeButton.setTouchable(Touchable.disabled);
                tReplayButton.setTouchable(Touchable.disabled);

                tReplayButton.addAction(sequence(moveBy(0, -tReplayButton.getHeight() / 4, .25f), moveBy(0, tReplayButton.getHeight() / 4, .25f), run(new Runnable() {
                    @Override
                    public void run() {

                        tReplayButton.addAction(fadeOut(.25f));
                        tScorePane.addAction(fadeOut(.25f));
                        tHomeButton.addAction(fadeOut(.25f));
                    }
                }), delay(.25f), run(new Runnable() {
                    @Override
                    public void run() {
                        tCluster.reset();
                        tWorld.addActor(tBackground);
                        tWorld.addActor(tCluster);
                        tMusic.setVolume(.1f);
                        resetGame();
                    }
                })));
                return true;
            }
        });
    }

    private void setUpHomeButtonListener() {
        tHomeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                tSound.play();
                tHomeButton.setTouchable(Touchable.disabled);
                tReplayButton.setTouchable(Touchable.disabled);

                tHomeButton.addAction(sequence(moveBy(0, -tHomeButton.getHeight() / 4, .25f), moveBy(0, tHomeButton.getHeight() / 4, .25f), run(new Runnable() {
                    @Override
                    public void run() {
                        tScorePane.addAction(fadeOut(.25f));
                        tHomeButton.addAction(fadeOut(.25f));
                        tReplayButton.addAction(fadeOut(.25f));
                    }
                }), delay(.25f), run(new Runnable() {
                    @Override
                    public void run() {
                        goHome();
                    }
                })));
                return true;
            }
        });
    }

    private void setUpBackButtonListener() {
        tBackButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                tBackButton.setTouchable(Touchable.disabled);
                tSound.play();
                tBackButton.addAction(sequence(moveBy(0, -tBackButton.getHeight() / 4, .25f), moveBy(0, tBackButton.getHeight() / 4, .25f), run(new Runnable() {
                    @Override
                    public void run() {
                        tCreditsPane.addAction(fadeOut(.25f));
                        tBackButton.addAction(fadeOut(.25f));
                    }
                }), delay(.25f), run(new Runnable() {
                    @Override
                    public void run() {
                        goHome();
                    }
                })));
                return true;
            }
        });
    }

    private void setUpCreditsButtonListener() {
        tCreditsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                tSound.play();
                tCreditsButton.setTouchable(Touchable.disabled);
                tCreditsButton.addAction(sequence(moveBy(0, -tCreditsButton.getHeight() / 4, .25f), moveBy(0, tCreditsButton.getHeight() / 4, .25f), run(new Runnable() {
                    @Override
                    public void run() {
                        tCreditsButton.addAction(fadeOut(.25f));
                        tTutorialButton.addAction(fadeOut(.25f));

                    }
                }), delay(.25f), run(new Runnable() {
                    @Override
                    public void run() {
                        tMusic.setVolume(.05f);
                        tCreditsPane.addAction(Actions.moveTo(tViewport.getScreenWidth() / 2 - tCreditsPane.getWidth() / 2, tViewport.getScreenHeight() / 2 - tCreditsPane.getHeight() / 2, .1f));
                        tBackButton.addAction(Actions.moveTo(tViewport.getScreenWidth() / 50, tViewport.getScreenWidth() / 50, .1f));
                        Gdx.input.setInputProcessor(tCreditsUI);
                    }
                })));
                return true;
            }

        });
    }

    private void setUpTutorialButtonListener() {
        tTutorialButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                tSound.play();

                tTutorialButton.setTouchable(Touchable.disabled);
                tTutorialButton.addAction(sequence(moveBy(0, -tCreditsButton.getHeight() / 4, .25f), moveBy(0, tCreditsButton.getHeight() / 4, .25f), run(new Runnable() {
                    @Override
                    public void run() {
                        tTutorialButton.addAction(fadeOut(.25f));
                        tCreditsButton.addAction(fadeOut(.25f));
                        tTutorialButton.setTouchable(Touchable.enabled);
                    }
                }), delay(.25f), run(new Runnable() {
                    @Override
                    public void run() {
                        tMusic.setVolume(.05f);
                        reset();
                        Gdx.input.setInputProcessor(tTutorialUI);
                    }
                })));
                return true;
            }

        });
    }

    private void goHome() {
        reset();
        tMusic.setVolume(1.0f);


        tTimeLeft = 10;
        tLevel = 1;

        Gdx.input.setInputProcessor(tTitleUI);
    }

    private void reset() {
        tHomeButton.reset();
        tReplayButton.reset();
        tBackButton.reset();
        tCluster.reset();
        tTutorialButton.reset();
        tTapPrompt.reset();
        tScorePane.reset();
        tBackground.reset();
        tCreditsPane.reset();
        tCreditsButton.reset();


        tTimeLeft = 10;
        tLevel = 1;
    }


}


