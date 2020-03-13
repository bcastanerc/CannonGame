import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.awt.*;
import java.awt.Font;
import java.io.IOException;

public class MainGame {
    static final int WIDTH = 1024;
    static final int HEIGHT = 576;
    static boolean SCREEN = false;
    public static void main(String[] args) throws SlickException {

       CannonGame CaGame = new CannonGame("Cannon Game!!");
       AppGameContainer ap = new AppGameContainer(CaGame, WIDTH, HEIGHT, SCREEN);
       ap.setTargetFrameRate(60);
       ap.start();

    }
}

class CannonGame extends BasicGame{
   // private Font fuente;
    Font font;
    TrueTypeFont ttf;
    private Cannon cannon;
    private Ball ball;
    private Target target;
    private Landscape landscape;
    private int score = 0;

    public CannonGame(String title) throws SlickException {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {

        /*try {
            fuente = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
                    org.newdawn.slick.util.ResourceLoader.getResourceAsStream("resources\\WHITRABT.TTF"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        font = new UnicodeFont(fuente);*/

        font = new Font("resources\\WHITRABT.TTF", Font.BOLD, 20);
        ttf = new TrueTypeFont(font, true);

        landscape = new Landscape();
        cannon = new Cannon();
        target = new Target();
        ball = new Ball();

    }

    @Override
    public void update(GameContainer gameContainer, int i) throws SlickException {
        Input input = gameContainer.getInput();

        landscape.update();

        if (input.isKeyDown(Input.KEY_UP) && this.cannon.getCannonRotation() > -90){
            this.cannon.setCannonRotation((float) (this.cannon.getCannonRotation()-1));
        }
        else if (input.isKeyDown(Input.KEY_DOWN) && this.cannon.getCannonRotation() < 0){
            this.cannon.setCannonRotation((float) (this.cannon.getCannonRotation()+1));
        }

        if (input.isKeyDown(Input.KEY_RIGHT) && this.cannon.getStrength() < 100){
            this.cannon.setStrength(this.cannon.getStrength()+1);
        }
        else if (input.isKeyDown(Input.KEY_LEFT) && this.cannon.getStrength() > 0){
            this.cannon.setStrength(this.cannon.getStrength()-1);
        }

        if (input.isKeyPressed(Input.KEY_SPACE) && this.cannon.getShootsRemaining() > 0){
            this.cannon.fire(ball);
        }

    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) throws SlickException {
        this.landscape.render();
        this.cannon.render();
        ttf.drawString(100,5,"Angulo: " + Math.abs(this.cannon.getCannonRotation()));
        ttf.drawString(250,5,"Fuerza: " + Math.abs(this.cannon.getStrength()));
        ttf.drawString(400,5,"Disparos: " + this.cannon.getShootsRemaining());
        ttf.drawString(550,5,"Puntuación: " + score);

        //font.drawString(50,50, "Angulo: " + Math.abs(this.cannon.getCannonRotation()));
    }
}

class Cannon {
    private Image cannonBase;
    private Image cannon;
    private float rotation;
    private double strength;
    private final int cannonBaseX = 40;
    private final int cannonBaseY = 500;
    private int shootsRemaining = 5;

    Cannon() throws SlickException {
        cannonBase = new Image("resources\\cannon_base.png");
        cannon = new Image("resources\\cannon.png");
        // Rotación por defecto.
        rotation = -45;
        // Fuerza por defecto.
        strength = 0;
    }

    public void update(GameContainer gameContainer){

    }
    public void render(){
        // Para cuadrar el cañon lo desplazamos 10px a la izquierda y 40 arriba.
        cannon.draw(cannonBaseX,cannonBaseY-15);
        // Width -> 130/4 = 32.5, Height -> 74/2 = 37.
        cannon.setCenterOfRotation(cannon.getWidth()/4,cannon.getHeight()/2);
        cannon.setRotation(rotation);
        cannonBase.draw(cannonBaseX,cannonBaseY);
    }

    public void fire(Ball ball){
        shootsRemaining--;
        System.out.println(shootsRemaining);
    }

    public double getCannonRotation() {
        return rotation;
    }
    public double getStrength() {
        return strength;
    }

    public void setCannonRotation(float rotation) {
        this.rotation = rotation;
    }
    public void setStrength(double strength) {
        this.strength = strength;
    }

    public Image getCannon() {
        return cannon;
    }
    public Image getCannonBase() {
        return cannonBase;
    }

    public int getCannonBaseX() {
        return cannonBaseX;
    }
    public int getCannonBaseY() {
        return cannonBaseY;
    }

    public void setCannon(Image cannon) {
        this.cannon = cannon;
    }
    public void setCannonBase(Image cannonBase) {
        this.cannonBase = cannonBase;
    }

    public int getShootsRemaining() {
        return shootsRemaining;
    }
    public void setShootsRemaining(int shootsRemaining) {
        this.shootsRemaining = shootsRemaining;
    }
}

class Landscape{
    private Image background;
    private Image cloud;
    private int cloudY = 100;
    private int cloudX = 0;
    private boolean movingBack = false;
    Landscape() throws SlickException {
        background = new Image("resources\\landscape.jpg");
        cloud = new Image("resources\\cloud.png");
    }
    public void update(){
        // Movimmiento de la nuve.
        if (cloudX == (background.getWidth() -cloud.getWidth())) movingBack = true;
        if (movingBack && cloudX == 0) movingBack = false;
        if(!movingBack) cloudX++;
        else cloudX--;
    }
    public void render(){
        background.draw(0,0);
        cloud.draw(cloudX,cloudY);

    }

    public Image getBackground() {
        return background;
    }
    public void setBackground(Image background) {
        this.background = background;
    }

    public void setCloud(Image cloud) {
        this.cloud = cloud;
    }
    public Image getCloud() {
        return cloud;
    }
}

class Target{
    private Image target;
    private Shape shape;
    private int posX;
    private int posY = 500;
    Target() throws SlickException {
        target = new Image("resources\\target.png");
    }

    public void update(){

    }
    public void render(){

    }

    public boolean hit(){

        return false;
    }
    public void reset(){
        posX = (int)(Math.random() * 512) + 512;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosX() {
        return posX;
    }

    public Image getTarget() {
        return target;
    }

    public void setTarget(Image target) {
        this.target = target;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }
}

class Ball{
    private Image ball;
    private Target target;
    private Shape shape;
    Ball() throws SlickException {
        ball = new Image("resources\\ball.png");
    }

    public void update(){

    }
    public void render(){
    }
}



