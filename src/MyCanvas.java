import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color; 

public class MyCanvas{
    public Canvas canvas;
    public GraphicsContext gc;
    public Canvas draft;
    public GraphicsContext gcf;
    public MyBrush myBrush = new MyBrush(gcf); 

    private BoxBlur blur = new BoxBlur();

    public MyCanvas(double width, double height){
        canvas = new Canvas(width, height);
        draft = new Canvas(width, height);

        gc = canvas.getGraphicsContext2D();
        gcf = draft.getGraphicsContext2D();

        blur.setWidth(1);
        blur.setHeight(1);
        blur.setIterations(5);

        gc.setLineWidth(3);
        gcf.setLineWidth(3);
    }
    public void clearPage(){
        gc.setEffect(null);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setEffect(blur);
    }
    public void setLineWidth(int width){
        gc.setLineWidth(width);
        gcf.setLineWidth(width);
        myBrush.renew(gcf);
    }
    public void setFill(Color c){
        gc.setFill(c);
        gcf.setFill(c);
        myBrush.renew(gcf);
    }
    public void setStroke(Color c) {
        gc.setStroke(c);
        gcf.setStroke(c);
        myBrush.renew(gcf);
    }
    public GraphicsContext getGc(){
        return gc;
    }
    public GraphicsContext getGcf(){
        return gcf;
    }
}