import org.newdawn.slick.*;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

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

/**
 * Esta es la clase principal donde se encuentra el cuerpo del juego.
 */
class CannonGame extends BasicGame{

    Font gameFont;
    Font menuFont;
    private Cannon cannon;
    private Ball ball;
    private Target target;
    private Landscape landscape;
    private int score = 0;
    // Vatiable para saber el estado del juego.
    private boolean gameStarted = false;
    private Image startBG;
    private int consecutiveHits = 0;

    public CannonGame(String title) throws SlickException {
        super(title);
    }

    /**
     * En esta clase se inicializan los elementos del juego.
     * @param gameContainer contenerdor del CannonGame.
     * @throws SlickException excepcion de la libreria que estamos usando.
     */
    @Override
    public void init(GameContainer gameContainer) throws SlickException {

        this.gameFont = ResourceManager.getFont("resources/WHITRABT.TTF",20);
        this.menuFont = ResourceManager.getFont("resources/WHITRABT.TTF",35);
        startBG = new Image ("resources\\start.jpg");
        landscape = new Landscape();
        cannon = new Cannon();
        target = new Target();
    }

    /**
     * En esta cl
     * @param gameContainer contenerdor del CannonGame.
     * @param i  parametro interno de la libreria.
     * @throws SlickException excepcion de la libreria que estamos usando.
     */
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
            }

            // La Pelota por defecto es null hasta que no se pulsa el espacio, una vez se ha disparado se crea una.
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

    /**
     * En esta clase se hace el apartado grafico del juego.
     * @param gameContainer contenerdor del CannonGame.
     * @param graphics  parámetro interno de la libreria.
     * @throws SlickException excepcion de la libreria que estamos usando.
     */
    @Override
    public void render(GameContainer gameContainer, Graphics graphics) throws SlickException {
        if (!gameStarted){
            startBG.draw(0,0);
            this.menuFont.drawString(270,280,"Press SPACE to continue.");
        }else{
            this.landscape.render();
            this.target.render();

            // Si la pelota se ha disparado y ha caido resetea la pelota.
            if (ball != null){
                this.ball.render();
                if (target.hit(ball.getCircle())) {
                    this.ball=null;
                    consecutiveHits++;
                    // Al dar 3 golpes consecutivos da 1 disparo más
                    if (consecutiveHits == 3){
                        consecutiveHits = 0;
                        this.cannon.setShootsRemaining(this.cannon.getShootsRemaining()+1);
                    }
                    score+=100;
                    this.gameFont.drawString(this.target.getPosX()+55,this.target.getPosY()-40,"HIT");
                    this.target.reset();
                }
            }
            this.cannon.render();
            this.gameFont.drawString(100,7,"Angle: " + Math.abs(this.cannon.getCannonRotation()));
            this.gameFont.drawString(350,7,"Strenght: " + this.cannon.getCannonStrenght());
            this.gameFont.drawString(600,7,"Shoots: " + this.cannon.getShootsRemaining());
            this.gameFont.drawString(850,7,"Score: " + score);

        }
    }
}

/**
 * Clase cannon la cual simula el cañon del juego.
 */
class Cannon {

    private Image cannonBase;
    private Image cannon;
    private float rotation;
    private double cannonStrenght;
    private int shootsRemaining = 5;

    /**
     * Constructor del cañon.
     * @throws SlickException
     */
    Cannon() throws SlickException {
        cannonBase = new Image("resources\\cannon_base.png");
        cannon = new Image("resources\\cannon.png");
        // Rotación por defecto.
        rotation = -45;
        // Fuerza por defecto.
        cannonStrenght = 50;
    }

    /**
     * Parte gráfica del caón.
     */
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

    /**
     * Metodo de cannon el cual disàra la pelota.
     * @return devuelve una nueva pelota con sus caracteristicas.
     * @throws SlickException
     */
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

/**
 * Esta clase define el fondo del juego.
 */
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

    /**
     * Este método actualiza la posición de la nuve del fondo.
     */
    public void update(){
        // Movimmiento de la nuve.
        if (cloudX == (background.getWidth() -cloud.getWidth())) movingBack = true;
        if (movingBack && cloudX == 0) movingBack = false;
        if(!movingBack) cloudX++;
        else cloudX--;
    }

    /**
     * Este método pinta la la nuve y el fondo en el juego.
     */
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

/**
 * Clase que define al objetivo del juego.
 */
class Target{
    private Image target;
    private Shape rectangle;
    private int posX;
    private int posY = 500;

    /**
     * Por defecto le da una posición aleatoria al Target cada vez que se crea
     * @throws SlickException excepcion de la libreria que estamos usando.
     */
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

    /**
     *
     * @param shape grosor del target en pixeles
     * @return devuelve true si la pelota coincide con los pixeles de la posición del target y false no coincide.
     */
    public boolean hit(Shape shape){
        return rectangle.intersects(shape);
    }

    /**
     * Este método le da una nueva posición al objetivo.
     */
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

/**
 * Clase que representa a la pelota en el juego.
 */
class Ball{
    private Image ball = new Image("resources\\ball.png");
    private Shape circle;
    private int posX = 40;
    private int posY = 500;
    private int ballRotation = 0;
    private float pX = 0;
    private float pY= 0;
    private static final double GRAVITY = 9.8;
    private float time = 0;
    private float vx;
    private float vy;

    Ball(double rot, double str) throws SlickException {
        ball.setCenterOfRotation(ball.getWidth()/2,ball.getHeight()/2);
        // Multiplica la velocidad para que no haya que usar tanta fuerza en el cañon,le da un 20% más de fuerza al disparo.
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

    /**
     * Calcula si la pelota se ha caido de la pantalla. Al superar los 580px (height del juego) determina que ha caido.
     * @return devuelve true si ha caido, si no false.
     */
    public boolean fallen(){
        return pY >= 580;
    }

    /**
     * El método update de la clase Ball simula la parabola gracias a la gravedad, angulo y fuerza del cañon.
     */
    public void update(){
        pX = posX +vx*time;
        pY = (float) (posY +vy*time + GRAVITY*time*time/2f);
        time+= 0.15f;
        circle = new Circle(pX,pY,20);
        circle.setCenterX(pX+20);
        circle.setCenterY(pY+20);

    }

    /**
     * Este método pinta la pelota en el juego.
     */
    public void render(){
        ball.draw(pX,pY);
        ball.setRotation(ballRotation = ballRotation+5);
    }
}



