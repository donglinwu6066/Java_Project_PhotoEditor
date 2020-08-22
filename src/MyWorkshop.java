import java.util.ArrayList;
import javafx.scene.layout.StackPane;
import java.util.Collections;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent; 
import javafx.scene.image.Image;
import javafx.scene.SnapshotParameters;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color; 
import java.io.File;
import javafx.scene.image.WritableImage;
import java.io.IOException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Rotate;
import javafx.scene.control.Label;
import javafx.event.EventHandler;
import javafx.scene.robot.Robot;
import javafx.scene.image.PixelReader; 
import javafx.scene.image.PixelWriter; 
import javafx.scene.image.WritableImage;

public class MyWorkshop{
    public ArrayList<ArrayList<MyPane>> pane = new ArrayList<ArrayList<MyPane>>();
    public ArrayList<MyTable> table = new ArrayList<MyTable>();
    public ArrayList<StackPane> stack = new ArrayList<StackPane>();
    public ArrayList<Double> ratio = new ArrayList<Double>();
    //for stack
    public ArrayList<Integer> size = new ArrayList<Integer>();
    public ArrayList<Integer> current = new ArrayList<Integer>();
    //for files
    public int workSize = 0;
    public int workCurr = 0;
    
    public ArrayList<SceneGestures> sceneGestures = new ArrayList<SceneGestures>();
    public ArrayList<NodeGestures> nodeGestures = new ArrayList<NodeGestures>();

    
    public MyWorkshop(){
        
    }
    public void newWorkshop(double width, double height, String name){
        // 
        if(width>1297.6 || height>708){
            if(width/1297.6 > height/708){
                ratio.add(1297.6/width);
                height *=(1297.6/width); 
                width = 1297.6;} 
            else{
                ratio.add(708/height);
                width *= (708/height); 
                height = 708;}
        }
        else{ratio.add(1.);}
        StackPane tempStack = new StackPane();
        System.out.printf("%f, %f\n",width, height);
        tempStack.setMaxSize(width, height);
        tempStack.setAlignment(Pos.CENTER);
        stack.add(tempStack);
        
        ArrayList<MyPane> paneList = new ArrayList<MyPane>();
        MyPane tempPane = new MyPane(width, height);
        //pane.setAlignment(Pos.CENTER);
 
        setGesture(tempPane);
        paneList.add(tempPane);
        pane.add(paneList);
        stack.get(workSize).getChildren().add(tempPane);
        
        table.add(new MyTable());
        table.get(workSize).addLayer(name);
        
        size.add(1);
        current.add(0);

        workSize++;
    }
    public void getPositionLabel(Label label) {
        // Label label = new Label("Outside Label");
        
        pane.get(workCurr).get(current.get(workCurr)).setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                Robot r = new Robot();
                Color color = r.getPixelColor(event.getScreenX(), event.getScreenY());
                String msg =
                "(x: "+ event.getX()+", y: "+ event.getY()+") "+"Color:" + 	color ;
                label.setText(msg);
            }
        });
      
        pane.get(workCurr).get(current.get(workCurr)).setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                label.setText("Outside Pane");
            }
        });
    }
    // public void setFilter(){
        
    // }
    public void addImage( Image image){
        pane.get(workCurr).get(current.get(workCurr)).addImage(image, nodeGestures.get(workCurr));
    }
    public void setCurrent(int num){
        if(num<size.get(workCurr)){
            current.set(workCurr, num);
        }
        else{System.out.println("Table size are smaller than current");}
    }
    public void setWorkCurr(int num){
        if(num<workSize){
            workCurr = num;
        }
        else{System.out.println("workSize size are smaller than workCurr");}
    }
    public void swapCan(int start, int end){
        pane.get(workCurr).get(current.get(workCurr)).swapCanvas(start, end);
    }
    public void addPane(double width, double height){
        MyPane tempPane = new MyPane(width, height);
        //pane.setAlignment(Pos.CENTER);
        setGesture(tempPane);
        pane.get(workCurr).add(tempPane);
        stack.get(workCurr).getChildren().add(tempPane);
    }
    public void addLayer(){
        table.get(workCurr).addlayerWindow();
        
    }
    public void deleteLayer(int delete){
        table.get(workCurr).deletelayerWindow(delete);
    }
    private void setGesture(MyPane pane){
        sceneGestures.add(new SceneGestures(pane));
        nodeGestures.add(new NodeGestures(pane));

        stack.get(workSize).addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.get(workSize).getOnMousePressedEventHandler());
        stack.get(workSize).addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.get(workSize).getOnMouseDraggedEventHandler());
        stack.get(workSize).addEventFilter( ScrollEvent.ANY, sceneGestures.get(workSize).getOnScrollEventHandler());
    }
    public void deletePane(int num){
        if(num<size.get(workCurr)){
            pane.get(workCurr).remove(num);
            stack.remove(num);
            table.remove(num);
            size.set(workCurr, size.get(workCurr)-1);
            if(size.get(workCurr) <= current.get(workCurr)){
                current.set(workCurr, current.get(workCurr)-1);
            }
        }
    }
    public void screenshot(File file, boolean bgStatus, double scaling){
        try {
            pane.get(workCurr).get(current.get(workCurr)).setScale(1);
            pane.get(workCurr).get(current.get(workCurr)).goPivotOri();
            SnapshotParameters parameters = new SnapshotParameters();   
            if(bgStatus){
                pane.get(workCurr).get(current.get(workCurr)).setStyle("-fx-background-color: transparent;");
            }
            parameters.setFill(Color.TRANSPARENT);
            parameters.setTransform(new Scale(scaling/ratio.get(workCurr), scaling/ratio.get(workCurr)));
            //save
            WritableImage snapshot = stack.get(workCurr).snapshot(parameters, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot,null),"png",file);
            pane.get(workCurr).get(current.get(workCurr)).setStyle("-fx-background-color: white;");
        } catch (IOException ex) { ex.printStackTrace(); }
    }
    public void rotatePane(Rotate rotate){
        stack.get(workCurr).getTransforms().add(rotate);
    }
    public void clearRotate(){
        for(int i=0 ; i<5 ; i++){
            stack.get(workCurr).getTransforms().clear();
        }
    }
    public void setImageView(Image image){
        pane.get(workCurr).get(current.get(workCurr)).imageView.setImage(image);
    }
    public void swapPane(int a, int b){
        Collections.swap(table, a, b);
        Collections.swap(stack, a, b);
    }
    public void clearAll(){
        for(int i=0 ; i<workSize ; i++){
            pane.get(i).clear();
        }
        pane.clear();
        table.clear();
        stack.clear();
        size.clear();
        current.clear();
        sceneGestures.clear();
        nodeGestures.clear();
        workSize = 0;
        workCurr = 0;
    }
}