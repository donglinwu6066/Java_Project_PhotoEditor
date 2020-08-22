import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.PixelReader; 
import javafx.scene.image.PixelWriter; 
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.robot.Robot;
import java.lang.Math;
import java.nio.IntBuffer;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public interface PainterTool {

    void onPress(MouseEvent e, GraphicsContext g);

    void onDrag(MouseEvent e, GraphicsContext g);

    void onRelease(MouseEvent e, GraphicsContext g);

    String getName();
}
//----------------------------------------------------------------------
class MyBrush{
    public PainterTool painter = null;
    private Pen penTool = new Pen();
    private Eraser eraserTool = new Eraser();
    private Line lineTool;
    private Oval ovalTool;
    private Rectangle rectTool;

    public MyBrush(GraphicsContext gc){
        lineTool = new Line(gc);
        ovalTool = new Oval(gc);
        rectTool = new Rectangle(gc);
    }
    public void renew(GraphicsContext gc){
        lineTool.update(gc);
        ovalTool.update(gc);
        rectTool.update(gc);
    }
    public void setMode(int mode){
        if(mode == 0){
            painter = null;
        }
        else if(mode == 1){
            painter = penTool;
        }
        else if(mode == 2){
            painter = eraserTool;
        }
        else if(mode == 3){
            painter = lineTool;
        }
        else if(mode == 4){
            painter = ovalTool;
        }
        else if(mode == 5){
            painter = rectTool;
        }
    }
}
//
class Pen implements PainterTool {
    double preX;
    double preY;
    double width;
    public Pen() {
    }

    public void onPress(MouseEvent e, GraphicsContext g) {
        preX = e.getX();
        preY = e.getY();
        width = g.getLineWidth();
        g.beginPath();
    }

    public void onDrag(MouseEvent e, GraphicsContext g) {
        
        double dis;
        dis = distance(preX, preY, e.getX(), e.getY());
        if(dis > 3){
            g.setLineWidth(3/dis*width);
            //System.out.printf("%f ", 3/dis*width);
        }
        
        g.lineTo(e.getX(), e.getY());
        g.stroke();
        preX = e.getX();
        preY = e.getY();
    }

    public void onRelease(MouseEvent e, GraphicsContext g) {
        g.closePath();
        g.setLineWidth(width);
    }

    public String getName() {
        return "Pen";
    }
    private double distance(double x1, double y1, double x2, double y2){
        return Math.abs(Math.sqrt(x1*x1+y1*y1) - Math.sqrt(x2*x2+y2*y2));
    }
}

//----------------------------------------------------------------------
class Eraser implements PainterTool {

    public Eraser() {
    }

    public void onPress(MouseEvent e, GraphicsContext g) {
        g.beginPath();
    }

    public void onDrag(MouseEvent e, GraphicsContext g) {
        g.setEffect(null);
        g.clearRect(e.getX()-g.getLineWidth(), e.getY()-g.getLineWidth(), 2*g.getLineWidth(), 2*g.getLineWidth());
    }

    public void onRelease(MouseEvent e, GraphicsContext g) {
        g.setEffect(new BoxBlur(1,1,1));
    }

    public String getName() {
        return "Eraser";
    }
}
//----------------------------------------------------------------------
class Line implements PainterTool {

    private double startingX;
    private double startingY;
    private GraphicsContext tempG;

    public Line(GraphicsContext tempG) {
        this.tempG = tempG;
    }
    public void update(GraphicsContext tempG) {
        this.tempG = tempG;
    }
    public void onPress(MouseEvent e, GraphicsContext g) {
        startingX = e.getX();
        startingY = e.getY();
        g.beginPath();
        g.moveTo(startingX, startingY);
    }

    public void onDrag(MouseEvent e, GraphicsContext g) {
        tempG.clearRect(0, 0, tempG.getCanvas().getWidth(),
                             tempG.getCanvas().getHeight());
        tempG.beginPath();
        tempG.moveTo(startingX, startingY);
        tempG.lineTo(e.getX(), e.getY());
        tempG.stroke();
        tempG.closePath();
    }

    public void onRelease(MouseEvent e, GraphicsContext g) {
        tempG.clearRect(0, 0, tempG.getCanvas().getWidth(),
                             tempG.getCanvas().getHeight());
        g.lineTo(e.getX(), e.getY());
        g.stroke();
        g.closePath();
    }

    public String getName() {
        return "Line";
    }
}
//----------------------------------------------------------------------
class Oval implements PainterTool {

    private double startingX;
    private double startingY;
    private GraphicsContext tempG;
    private double width;
    private double height;
    private double topLeftX;
    private double topLeftY;

    public Oval(GraphicsContext tempG) {
        this.tempG = tempG;
    }
    public void update(GraphicsContext tempG) {
        this.tempG = tempG;
    }
    public void onPress(MouseEvent e, GraphicsContext g) {
        startingX = e.getX();
        startingY = e.getY();
    }

    public void onDrag(MouseEvent e, GraphicsContext g) {
        tempG.clearRect(0, 0, tempG.getCanvas().getWidth(),
                             tempG.getCanvas().getHeight());
        width = Math.abs(e.getX() - startingX);
        height = Math.abs(e.getY() - startingY);
        topLeftX = startingX < e.getX() ? startingX : e.getX();
        topLeftY = startingY < e.getY() ? startingY
            : e.isShiftDown() ? startingY - width : e.getY();
        tempG.fillOval(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
        tempG.strokeOval(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
        
    }

    public void onRelease(MouseEvent e, GraphicsContext g) {
        tempG.clearRect(0, 0, tempG.getCanvas().getWidth(),
                             tempG.getCanvas().getHeight());
        g.fillOval(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
        g.strokeOval(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
    }

    public String getName() {
        return "oval";
    }
}
//----------------------------------------------------------------------
class Rectangle implements PainterTool {

    private double startingX;
    private double startingY;
    private GraphicsContext tempG;
    private double width;
    private double height;
    private double topLeftX;
    private double topLeftY;

    public Rectangle(GraphicsContext tempG) {
        this.tempG = tempG;
    }
    public void update(GraphicsContext tempG) {
        this.tempG = tempG;
    }
    public void onPress(MouseEvent e, GraphicsContext g) {
        startingX = e.getX();
        startingY = e.getY();
    }

    public void onDrag(MouseEvent e, GraphicsContext g) {
        tempG.clearRect(0, 0, tempG.getCanvas().getWidth(),
                             tempG.getCanvas().getHeight());
        width = Math.abs(e.getX() - startingX);
        height = Math.abs(e.getY() - startingY);
        topLeftX = startingX < e.getX() ? startingX : e.getX();
        topLeftY = startingY < e.getY() ? startingY
            : e.isShiftDown() ? startingY - width : e.getY();
        tempG.fillRect(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
        tempG.strokeRect(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
    }

    public void onRelease(MouseEvent e, GraphicsContext g) {
        tempG.clearRect(0, 0, tempG.getCanvas().getWidth(),
                             tempG.getCanvas().getHeight());
        g.fillRect(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
        g.strokeRect(topLeftX, topLeftY, width,
            e.isShiftDown() ? width : height);
    }

    public String getName() {
        return "Rectangle";
    }
}
//----------------------------------------------------------------------
/*
class Blur implements PainterTool {
    WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
    PixelReader pr;
    PixelWriter pw;
    // private int extent;
    Robot ro = new Robot();
    ValueOfARGB v=new ValueOfARGB();
    int a,r,g,b,p;
    private class ValueOfARGB{
        public int a,r,g,b;
        public ValueOfARGB(){
            a = r = g = b = 0;
        } 
    }

    public Blur(GraphicsContext gr) {
        pr = gr.getGraphicsContext2D().getPixelWriter();
        pw = gr.getGraphicsContext2D().getPixelWriter();
        a = r = g = b = 0;
    }

    public void onPress(MouseEvent e, GraphicsContext gr) {
        /*
        array=new ValueOfARGB [7][7];
        for(int lr=-3 ; lr<=3 ; lr++){
            for(int tb=-3 ; tb<=3 ; tb++){
                for(int i=e.getX()-3+lr;i<=e.getX()+3+lr;i++){
                    for(int j=e.getY()-3+tb;j<e.getY+3+tb;j++){
                        p = r.getPixelColor(e.getScreenX(), e.getScreenY());
                        a = (p>>24)&0xff; 
                        r = (p>>16)&0xff; 
                        g = (p>>8)&0xff; 
                        b = p&0xff;
                        v.a+=a;
                        v.r+=r;
                        v.g+=g;
                        v.b+=b;
                    }
                }
                v.a/=49;
                v.r/=49;
                v.g/=49;
                v.b/=49;
                p = (a<<24) | (r<<16) | (g<<8) | b; 
                pixelWriter.setArgb(e.getX(), e.getX(), p); 
            }
        }
    }

    public void onDrag(MouseEvent e, GraphicsContext gr) {
        // array=new ValueOfARGB [7][7];
        for(int lr=-3 ; lr<=3 ; lr++){
            for(int tb=-3 ; tb<=3 ; tb++){
                for(int i=(int)e.getX()-3+lr;i<=(int)e.getX()+3+lr;i++){
                    for(int j=(int)e.getY()-3+tb;j<(int)e.getY()+3+tb;j++){
                        p = (int)ro.getPixelColor((int)e.getScreenX(), (int)e.getScreenY());
                        a = (int)((p>>24)&0xff); 
                        r = (int)((p>>16)&0xff); 
                        g = (int)((p>>8)&0xff); 
                        b = (int)p&0xff;
                        v.a+=a;
                        v.r+=r;
                        v.g+=g;
                        v.b+=b;
                    }
                }
                v.a/=49;
                v.r/=49;
                v.g/=49;
                v.b/=49;
                p = ((a<<24) | (r<<16) | (g<<8) | b); 
                pixelWriter.setArgb(e.getX(), e.getX(), p); 
            }
        }
    }

    public void onRelease(MouseEvent e, GraphicsContext g) {
    }

    public String getName() {
        return "Blur";
    }

    //-----------------------------
    
}
*/