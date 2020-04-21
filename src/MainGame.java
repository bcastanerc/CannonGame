import org.newdawn.slick.*;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import java.awt.*;

public class MainGame {
    static final int WIDTH = 1024;
    static final int HEIGHT = 576;
    static boolean SCREEN = false;
    public static void main(String[] args) throws SlickException {

       CannonGame CaGame = new CannonGame("Cannon Game!!");
       AppGameContainer ap = new AppGameContainer(CaGame, WIDTH, HEIGHT, SCREEN);
       ap.setTargetFrameRate(59);
       ap.start();

    }
}

class CannonGame extends BasicGame{
   // private Font fuente;
    Font gameFont;
    Font menuFont;
    private Cannon cannon;
    private Ball ball;
    private Target target;
    private Landscape landscape;
    private int score = 0;
    private boolean gameStarted = false;
    private Image startBG;

    public CannonGame(String title) throws SlickException {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) throws SlickException {

        this.gameFont = ResourceManager.getFont("resources/WHITRABT.TTF",20);
        this.menuFont = ResourceManager.getFont("resources/WHITRABT.TTF",35);
        startBG = new Image ("resources\\start.jpg");
        landscape = new Landscape();
        cannon = new Cannon();
        target = new Target();
    }

    @Override
    public void update(GameContainer gameContainer, int i) throws SlickException {
        Input input = gameContainer.getInput();

        if (!gameStarted && input.isKeyPressed(Input.KEY_SPACE)){
            gameStarted = true;
        }else{
            landscape.update();

            // Si se pulsa la dirección hacia arriba o abajo se cambia el angulo del disparo.
            if (input.isKeyDown(Input.KEY_UP) && this.cannon.getCannonRotation() > -90){
                this.cannon.setCannonRotation((float) (this.cannon.getCannonRotation()-1));
            }
            else if (input.isKeyDown(Input.KEY_DOWN) && this.cannon.getCannonRotation() < 0){
                this.cannon.setCannonRotation((float) (this.cannon.getCannonRotation()+1));
            }

            // Si se pulsa la dirección hacia la derecha o a la izquierda se cambia la fuerza del disparo.
            if (input.isKeyDown(Input.KEY_RIGHT) && this.cannon.getCannonStrenght() < 100){
                this.cannon.setCannonStrenght(this.cannon.getCannonStrenght()+1);
            }
            else if (input.isKeyDown(Input.KEY_LEFT) && this.cannon.getCannonStrenght() > 10){
                this.cannon.setCannonStrenght(this.cannon.getCannonStrenght()-1);
            }

            // Cuando se pulsa el espacio dispara la pelota.
            if (input.isKeyPressed(Input.KEY_SPACE) && this.cannon.getShootsRemaining() > 0 && gameStarted && this.ball==null){
               ball = this.cannon.fire();
                // Le da una nueva posición al Target
            }

            //
            if (ball != null){
                ball.update();
                if (this.ball.fallen()){
                    if(score > 30) score-=30;
                    cannon.setShootsRemaining(cannon.getShootsRemaining()-1);
                    this.ball = null;
                    this.target.reset();
                }
            }

        }

    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) throws SlickException {
        if (!gameStarted){
            startBG.draw(0,0);
            this.menuFont.drawString(270,280,"Press SPACE to continue.");
        }else{

            // Si la pelota se ha disparado y ha caido resetea la pelota.
            this.landscape.render();
            this.target.render();
            if (ball != null){
                this.ball.render();
                if (target.hit(ball.getCircle())) {
                    this.ball=null;
                    score+=100;
                    this.target.reset();
                }
            }
            this.cannon.render();
            graphics.draw(target.getShape());
            this.gameFont.drawString(100,7,"Angle: " + Math.abs(this.cannon.getCannonRotation()));
            this.gameFont.drawString(350,7,"Strenght: " + this.cannon.getCannonStrenght());
            this.gameFont.drawString(600,7,"Shoots: " + this.cannon.getShootsRemaining());
            this.gameFont.drawString(850,7,"Score: " + score);

        }
    }
}

class Cannon {
    private Image cannonBase;
    private Image cannon;
    private float rotation;
    private double cannonStrenght;
    private int shootsRemaining = 5;

    Cannon() throws SlickException {
        cannonBase = new Image("resources\\cannon_base.png");
        cannon = new Image("resources\\cannon.png");
        // Rotación por defecto.
        rotation = -45;
        // Fuerza por defecto.
        cannonStrenght = 50;
    }

    public void update(GameContainer gameContainer){

    }

    public void render(){
        // Para cuadrar el cañon lo desplazamos 10px a la izquierda y 40 arriba.
        int cannonBaseX = 40;
        int cannonBaseY = 500;
        cannon.draw(cannonBaseX, cannonBaseY -15);
        // Width -> 130/4 = 32.5, Height -> 74/2 = 37.
        cannon.setCenterOfRotation(cannon.getWidth()/4,cannon.getHeight()/2);
        cannon.setRotation(rotation);
        cannonBase.draw(cannonBaseX, cannonBaseY);
    }

    public Ball fire() throws SlickException {
       return new Ball(Math.abs(this.getCannonRotation()),this.getCannonStrenght());
    }

    public double getCannonRotation() {
        return rotation;
    }
    public double getCannonStrenght() {
        return cannonStrenght;
    }

    public void setCannonRotation(float rotation) {
        this.rotation = rotation;
    }
    public void setCannonStrenght(double cannonStrenght) {
        this.cannonStrenght = cannonStrenght;
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
    private Shape rectangle;
    private int posX;
    private int posY = 500;
    Target() throws SlickException {
        target = new Image("resources\\target.png");
        posX = (int)(Math.random() * 512) + (512 - target.getWidth());
        rectangle = new Rectangle(posX,500,130,59);
    }

    public void update(){

    }
    public void render(){
        target.draw(posX,posY);
    }

    public boolean hit(Shape shape){
        return rectangle.intersects(shape);
    }
    public void reset(){
        posX = (int)(Math.random() * 512) + (512 - target.getWidth());
        rectangle.setX(posX);
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
        this.rectangle = shape;
    }

    public Shape getShape() {
        return rectangle;
    }
}

class Ball{
    private Image ball = new Image("resources\\ball.png");
    private Shape circle;
    private int posX = 40;
    private int posY = 500;
    private int ballRotation = 0;
    private float pX = 0;
    private float pY= 0;
    // La fuerza de la gravedad inferior a la de la tierra.
    private static final double GRAVITY = 9.8;
    private float time = 0;
    private float vx;
    private float vy;

    Ball(double rot, double str) throws SlickException {
        ball.setCenterOfRotation(ball.getWidth()/2,ball.getHeight()/2);
        System.out.println("Fuerza:" + str + "Rotación:" + rot);
        // Multiplica la velocidad para que no haya que usar tanta fuerza en el cañon,le da un 30% más de fuera al disparo.
        float vel = (float) ((float) str * 1.2);
        float angle = (float) (rot * Math.PI / 180f);
        vx = (float) (vel * Math.cos(angle));
        vy = (float) (-1*(vel * Math.sin(angle)));
    }

    public int getPosX() {
        return posX;
    }
    public int getPosY() {
        return posY;
    }

    public Shape getCircle() {
        return circle;
    }

    public void setCircle(Shape circle) {
        this.circle = circle;
    }

    public boolean fallen(){
        return pY >= 580;
    }

    public void update(){

        pX = posX +vx*time;
        pY = (float) (posY +vy*time + GRAVITY*time*time/2f);
        System.out.printf("Tiempo: %.1f Posición: (PosX: %.1f || PosY: %.1f)\n",time,pX,pY);
        time+= 0.15f;
        circle = new Circle(pX,pY,20);
        circle.setCenterX(pX+20);
        circle.setCenterY(pY+20);

    }
    public void render() throws SlickException {
        ball.draw(pX,pY);
        ball.setRotation(ballRotation = ballRotation+5);
    }
}



