import java.util.Collection;
import java.util.Collections;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

class MyPane extends Pane {
    public final MyCanvas[] myCanvas = new MyCanvas[10];
    public ImageView imageView = null;
    public DoubleProperty scaling = new SimpleDoubleProperty(1.0);
    int pivot = 0;

    public MyPane(double width, double height) {
        setStyle("-fx-background-color: white; ");
        for(int i=0 ; i<10 ; i++){
            MyCanvas tempCanvas = new MyCanvas(width, height);
            
            tempCanvas.draft.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (tempCanvas.myBrush.painter != null) {
                tempCanvas.myBrush.painter.onPress(e, tempCanvas.getGc());
            }
            });
            tempCanvas.draft.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
                if (tempCanvas.myBrush.painter != null) {
                    tempCanvas.myBrush.painter.onDrag(e, tempCanvas.getGc());
                }
            });
            tempCanvas.draft.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                if (tempCanvas.myBrush.painter != null) {
                    tempCanvas.myBrush.painter.onRelease(e, tempCanvas.getGc());
                }
            });
            myCanvas[i] = tempCanvas;
            this.getChildren().addAll(myCanvas[i].canvas, myCanvas[i].draft);
            System.out.printf("%d", i);
        }
        this.setPrefSize(width, height);
        
        
        // add scale transform
        scaleXProperty().bind(scaling);
        scaleYProperty().bind(scaling);

        
    }
    public void toFront(){
        myCanvas[pivot].canvas.toFront();
        myCanvas[pivot].draft.toFront();
    }
    public void toBack(){
        myCanvas[pivot].canvas.toBack();
        myCanvas[pivot].draft.toBack();
    }
    public void setCanvas(int pivot){
        this.pivot = pivot;
    }
    public double getScale() {
        return scaling.get();
    }

    public void setScale( double scale) {
        scaling.set(scale);
    }
    public void swapCanvas(int start, int end){
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(this.getChildren());
        Collections.swap(workingCollection, start, end);
        this.getChildren().setAll(workingCollection);

        MyCanvas tempCanvas = myCanvas[start];
        myCanvas[start] = myCanvas[end];
        myCanvas[end] = tempCanvas;
    } 
    public void bubbleCanvas(int begin){
        MyCanvas tempCanvas = myCanvas[begin];
        for(int i = begin; i<9 ; i++){
            myCanvas[i] = myCanvas[i+1];
        }
        myCanvas[9] = tempCanvas;
    } 

    public void setPivot( double x, double y) {
        setTranslateX(getTranslateX()-x);
        setTranslateY(getTranslateY()-y);
    }
    public void goPivotOri() {
        setTranslateX(0);
        setTranslateY(0);
    }
    public void addText(String str, String family, boolean bold, boolean italic, int size, Color c, NodeGestures nodeGestures) {
        MyText myText = new MyText(str);
        myText.setFontStyle( family, bold, italic, size);
        myText.setFill(c);
        //myText.text.setAlignment(Pos.CENTER);
        myText.text.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        myText.text.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        super.getChildren().add(myText.text);
    }
    public void addImage(Image image, NodeGestures nodeGestures) {
        MyImage myImage = new MyImage(image);
        imageView = myImage.getImageView();
        super.getChildren().add(myImage.imageView);
    }

}


/**
 * Mouse drag context used for scene and nodes.
 */
class DragContext {

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;

}

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
class NodeGestures {

    private DragContext nodeDragContext = new DragContext();

    MyPane canvas;

    public NodeGestures( MyPane canvas) {
        this.canvas = canvas;

    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            // left mouse button => dragging
            if(!event.isSecondaryButtonDown()){
                nodeDragContext.mouseAnchorX = event.getSceneX();
                nodeDragContext.mouseAnchorY = event.getSceneY();

                Node node = (Node) event.getSource();

                nodeDragContext.translateAnchorX = node.getTranslateX();
                nodeDragContext.translateAnchorY = node.getTranslateY();
            }
            else if(event.isSecondaryButtonDown()){
                Node node = (Node) event.getSource();
                canvas.getChildren().remove(node);
            }

    }};
    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // left mouse button => dragging
            if( !event.isPrimaryButtonDown())
                return;

            double scale = canvas.getScale();

            Node node = (Node) event.getSource();

            node.setTranslateX(nodeDragContext.translateAnchorX + (( event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
            node.setTranslateY(nodeDragContext.translateAnchorY + (( event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

            event.consume();

        }
    };
    
}
/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
class SceneGestures {

    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;

    private DragContext sceneDragContext = new DragContext();

    MyPane canvas;

    public SceneGestures( MyPane canvas) {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            // right mouse button => panning
            if( !event.isSecondaryButtonDown())
                return;

            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = canvas.getTranslateX();
            sceneDragContext.translateAnchorY = canvas.getTranslateY();

        }

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // right mouse button => panning
            if( !event.isSecondaryButtonDown())
                return;

            canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
            canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

            event.consume();
        }
    };

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {

            double delta = 1.2;

            double scale = canvas.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            if (event.getDeltaY() < 0)
                scale /= delta;
            else
                scale *= delta;

            scale = clamp( scale, MIN_SCALE, MAX_SCALE);

            double f = (scale / oldScale)-1;

            double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth()/2 + canvas.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight()/2 + canvas.getBoundsInParent().getMinY()));

            canvas.setScale( scale);

            // note: pivot value must be untransformed, i. e. without scaling
            canvas.setPivot(f*dx, f*dy);

            event.consume();

        }

    };


    public static double clamp( double value, double min, double max) {

        if( Double.compare(value, min) < 0)
            return min;

        if( Double.compare(value, max) > 0)
            return max;

        return value;
    }
}
