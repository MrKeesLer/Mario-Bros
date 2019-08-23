package fr.mrkeesler.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import fr.mrkeesler.mariobros.MarioBros;
import fr.mrkeesler.mariobros.Scenes.Hud;
import fr.mrkeesler.mariobros.Sprites.Mario;
import fr.mrkeesler.mariobros.Tools.B2WorldCreator;

public class PlayScreen implements Screen {

    private MarioBros game;
    private TextureAtlas atlas;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Mario player;

    public PlayScreen(MarioBros game){
        this.atlas = new TextureAtlas("Mario_And_Enemies.pack");

        this.game = game;

        this.gamecam = new OrthographicCamera();
        this.gamePort = new FitViewport(MarioBros.V_WIDTH/MarioBros.PPM,MarioBros.V_HEIGHT/MarioBros.PPM,this.gamecam);
        this.hud = new Hud(game.batch);

        this.maploader = new TmxMapLoader();
        this.map = this.maploader.load("worlds/world_1-1.tmx");
        this.renderer = new OrthogonalTiledMapRenderer(this.map, 1 / MarioBros.PPM);
        this.gamecam.position.set(this.gamePort.getWorldWidth()/2, this.gamePort.getWorldHeight()/2 ,0);

        world = new World(new Vector2(0,-10),true);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world,map);

       this.player = new Mario(world,this);
    }

    public TextureAtlas getAtlas(){return this.atlas;}

    @Override
    public void show() {

    }

    public void handleInput(float dt){
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
            this.player.b2body.applyLinearImpulse(new Vector2(0,4f),this.player.b2body.getWorldCenter(),true);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && this.player.b2body.getLinearVelocity().x <= 2)
            this.player.b2body.applyLinearImpulse(new Vector2(0.1f,0), this.player.b2body.getWorldCenter(),true);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && this.player.b2body.getLinearVelocity().x >= -2)
            this.player.b2body.applyLinearImpulse(new Vector2(-0.1f,0), this.player.b2body.getWorldCenter(),true);
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f,6,2);
        player.update(dt);
        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world,gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        this.hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.gamePort.update(width,height);
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
