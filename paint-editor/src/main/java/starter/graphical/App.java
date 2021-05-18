package starter.graphical;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.effect.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.shape.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import org.w3c.dom.css.Rect;

import java.util.Optional;
import java.lang.String;
import java.io.*;
import java.util.ArrayList;
import java.lang.Math.*;


public class App extends Application {
    final int thick_1 = 5;
    final int thick_2 = 10;
    final int thick_3 = 15;

    Pane main_screen = new Pane();
    int thickness = thick_1;
    boolean save_updated = true;
    boolean was_dragged = false;
    boolean dragged = false;
    Color fillColor = Color.RED;
    Color lineColor = Color.BLACK;
    String cur_tool = "selection";
    Text text_line = new Text("Line Color Picker");
    Text text_fill = new Text("Fill Color Picker");
    Rectangle copied_rectangle = new Rectangle();
    Circle copied_circle = new Circle();
    Line copied_line = new Line();
    Rectangle cur_rectangle = new Rectangle();
    Circle cur_circle = new Circle();
    Line cur_line = new Line();
    Rectangle selected_rectangle;
    Circle selected_circle;
    Line selected_line;
    String selected_shape = "none";
    double cur_x;
    double cur_y;

    ColorPicker line_colorPicker = new ColorPicker();
    ColorPicker fill_colorPicker = new ColorPicker();
    Button thickness_button1;
    Button thickness_button2;
    Button thickness_button3;
    Line line1_thickness = new Line(0, 0, 100, 0);
    Line line2_thickness = new Line(0, 0, 100, 0);
    Line line3_thickness = new Line(0, 0, 100, 0);
    Button style_button1;
    Button style_button2;
    Button style_button3;
    Line line1_style = new Line(0, 0, 100, 0);
    Line line2_style = new Line(0, 0, 100, 0);
    Line line3_style = new Line(0, 0, 100, 0);
    double[] style1_array = {0d};
    double[] style2_array = {50d, 30d};
    double[] style3_array = {3d,30d,4d,31d};
    int style_num = 1;
    int style1_size = 0;
    int style2_size = 2;
    int style3_size = 4;
    String cur_save_file = "";
    Button selection_tool;
    Button erase_tool;
    Button line_tool;
    Button circle_tool;
    Button rectangle_tool;
    Button fill_tool;
BorderPane root = new BorderPane();
Scene scene = new Scene(root, 1600, 1200);

    private void save(String file_name) {
        FileWriter file = null;
        BufferedWriter writer = null;

        try {
            file = new FileWriter(file_name);
            writer = new BufferedWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            deselect_shape();
            for(Node n: main_screen.getChildren()){
                if(n instanceof Rectangle){
                    Rectangle r = (Rectangle) n;
                    String x = String.valueOf(r.getX());
                    String y = String.valueOf(r.getY());
                    String width = String.valueOf(r.getWidth());
                    String height = String.valueOf(r.getHeight());
                    String fillColor = ((Color)r.getFill()).toString();
                    String lineColor = ((Color)r.getStroke()).toString();
                    String stylenum = "1";
                    if(r.getStrokeDashArray().size() == 0){
                        stylenum = "1";
                    } else if(r.getStrokeDashArray().size() == 2){
                        stylenum = "2";
                    } else if(r.getStrokeDashArray().size() == 4){
                        stylenum = "3";
                    }
                    String lineWidth = String.valueOf(r.getStrokeWidth());
                    writer.write(
                            "rectangle" + "," + x + "," + y + "," + width + "," + height + "," + fillColor + "," +
                                    lineColor + "," + stylenum + "," + lineWidth + "\n"
                    );
                } else if(n instanceof Circle){
                    Circle c = (Circle) n;
                    String x = String.valueOf(c.getCenterX());
                    String y = String.valueOf(c.getCenterY());
                    String radius = String.valueOf(c.getRadius());
                    String fillColor = ((Color)c.getFill()).toString();
                    String lineColor = ((Color)c.getStroke()).toString();
                    String stylenum = "1";
                    if(c.getStrokeDashArray().size() == 0){
                        stylenum = "1";
                    } else if(c.getStrokeDashArray().size() == 2){
                        stylenum = "2";
                    } else if(c.getStrokeDashArray().size() == 4){
                        stylenum = "3";
                    }
                    String lineWidth = String.valueOf(c.getStrokeWidth());
                    writer.write(
                            "circle" + "," + x + "," + y + "," + radius  + "," + fillColor + "," +
                                    lineColor + "," + stylenum + "," + lineWidth + "\n"
                    );
                } else if(n instanceof Line){
                    Line l = (Line) n;
                    String x_start = String.valueOf(l.getStartX());
                    String y_start = String.valueOf(l.getStartY());
                    String x_end = String.valueOf(l.getEndX());
                    String y_end = String.valueOf(l.getEndY());
                    String lineColor = ((Color)l.getStroke()).toString();
                    String stylenum = "1";
                    if(l.getStrokeDashArray().size() == 0){
                        stylenum = "1";
                    } else if(l.getStrokeDashArray().size() == 2){
                        stylenum = "2";
                    } else if(l.getStrokeDashArray().size() == 4){
                        stylenum = "3";
                    }
                    String lineWidth = String.valueOf(l.getStrokeWidth());
                    writer.write(
                            "line" + "," + x_start + "," + y_start + "," + x_end + "," + y_end + "," +
                                    lineColor + "," + stylenum + "," + lineWidth + "\n"
                    );
                }
            }
            writer.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        save_updated = true;
    }

    private void load(String file_name) {
        FileReader file = null;
        BufferedReader reader = null;
        String[] values;
        // open input file
        main_screen.getChildren().setAll();
        try {
            file = new FileReader(file_name);
            reader = new BufferedReader(file);
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error :(");
            alert.setContentText("File not found");

            alert.showAndWait();
            return;
        }
        // read and process lines one at a time
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                values = line.split(",");
                String the_shape = values[0];
                if(the_shape.equals("rectangle")){
                    Rectangle r = new Rectangle();
                    r.setX(Double.valueOf(values[1]));
                    r.setY(Double.valueOf(values[2]));
                    r.setWidth(Double.valueOf(values[3]));
                    r.setHeight(Double.valueOf(values[4]));
                    r.setFill(Color.web(values[5]));
                    r.setStroke(Color.web(values[6]));
                    if(values[7] == "1"){
                        r.getStrokeDashArray().setAll();
                    } else if(values[7] == "2"){
                        r.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
                    } else if(values[7] == "3"){
                        r.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
                    }
                    r.setStrokeWidth(Double.valueOf(values[8]));
                    rect_mouse_events(r);
                    select_rect(r);
                    main_screen.getChildren().add(r);
                } else if(the_shape.equals("circle")){
                    Circle c = new Circle();
                    c.setCenterX(Double.valueOf(values[1]));
                    c.setCenterY(Double.valueOf(values[2]));
                    c.setRadius(Double.valueOf(values[3]));
                    c.setFill(Color.web(values[4]));
                    c.setStroke(Color.web(values[5]));
                    if(values[6] == "1"){
                        c.getStrokeDashArray().setAll();
                    } else if(values[6] == "2"){
                        c.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
                    } else if(values[6] == "3"){
                        c.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
                    }
                    c.setStrokeWidth(Double.valueOf(values[7]));
                    circle_mouse_events(c);
                    select_circle(c);
                    main_screen.getChildren().add(c);
                } else if(the_shape.equals("line")) {
                    Line l = new Line();
                    l.setStartX(Double.valueOf(values[1]));
                    l.setStartY(Double.valueOf(values[2]));
                    l.setEndX(Double.valueOf(values[3]));
                    l.setEndY(Double.valueOf(values[4]));
                    l.setStroke(Color.web(values[5]));
                    if(values[6] == "1"){
                        l.getStrokeDashArray().setAll();
                    } else if(values[6] == "2"){
                        l.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
                    } else if(values[6] == "3"){
                        l.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
                    }
                    l.setStrokeWidth(Double.valueOf(values[7]));
                    line_mouse_events(l);
                    select_line(l);
                    main_screen.getChildren().add(l);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        //menubar
        MenuBar menubar = new MenuBar();
        setupMenu(menubar);

        VBox vBox = new VBox(2);
        vBox.setSpacing(10);
        vBox.setBackground(new Background(new BackgroundFill(Color.LAVENDER, CornerRadii.EMPTY, Insets.EMPTY)));
//        vBox.setPrefWidth(stage.getX() * 0.05);

        // left column
        GridPane tool_palette = new GridPane();
        setupToolPalette(tool_palette);

        VBox lineColorPicker_box = new VBox();
        setupLineColorPicker(lineColorPicker_box);

        VBox fillColorPicker_box = new VBox();
        setupFillColorPicker(fillColorPicker_box);

        VBox line_thickness_box = new VBox();
        setupLineThickness(line_thickness_box);

        VBox line_style_box = new VBox();
        setupLineStyle(line_style_box);

        vBox.getChildren().addAll(tool_palette, lineColorPicker_box, fillColorPicker_box, line_thickness_box, line_style_box);

        // CANVAS

        main_screen.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        run_main_screen();


        // final
        root.setTop(menubar);
        root.setCenter(main_screen);
        root.setLeft(vBox);


        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.DELETE) || e.getCode().equals(KeyCode.BACK_SPACE)) {
                if (selected_shape == "rectangle") {
                    main_screen.getChildren().remove(selected_rectangle);
                    selected_shape = "none";
                } else if (selected_shape == "circle") {
                    main_screen.getChildren().remove(selected_circle);
                    selected_shape = "none";
                } else if (selected_shape == "line") {
                    main_screen.getChildren().remove(selected_line);
                    selected_shape = "none";
                }
            }
            if (e.getCode().equals(KeyCode.ESCAPE)) {
                if(cur_tool == "selection"){
                    disable_thick_buttons();
                    disable_colorpicker_buttons();
                }
                deselect_shape();
            }
            if (e.getCode().equals(KeyCode.C)) {
                System.out.println("c pressed");
                if(selected_shape == "circle" || selected_shape == "rectangle" || selected_shape == "line"){
                    copy();
                }
            }
            if (e.getCode().equals(KeyCode.V)) {
                System.out.println("v pressed");
                    paste();
            }
            if (e.getCode().equals(KeyCode.X)) {
                System.out.println("x pressed");
                if(selected_shape == "rectangle"){
                    copy();
                    main_screen.getChildren().remove(selected_rectangle);
                    selected_shape = "none";
                } else if(selected_shape == "circle"){
                    copy();
                    main_screen.getChildren().remove(selected_circle);
                    selected_shape = "none";
                } else if(selected_shape == "line"){
                    copy();
                    main_screen.getChildren().remove(selected_line);
                    selected_shape = "none";
                }
            }
        });
        stage.setTitle("SketchIt");
        stage.setResizable(true);

        stage.setMinWidth(640);
        stage.setMinHeight(540);
//        stage.setMaxWidth(1920);
//        stage.setMaxHeight(1440);
        stage.setMaxWidth(1600);
        stage.setMaxHeight(1200);

        stage.setScene(scene);
        stage.show();
    }

    String copied_shape;
    double copied_width;
    double copied_height;
    Color copied_fill_color;
    Color copied_line_color;
    int copied_style;
    double copied_line_width;

    double copied_startx;
    double copied_starty;
    double copied_endx;
    double copied_endy;

    public void copy(){
        if(selected_shape == "rectangle"){
            copied_shape = "rectangle";
            copied_width = selected_rectangle.getWidth();
            copied_height = selected_rectangle.getHeight();
            copied_fill_color = (Color)selected_rectangle.getFill();
            copied_line_color = (Color)selected_rectangle.getStroke();
            if(copied_rectangle.getStrokeDashArray().size() == 0){
                copied_style = 1;
            } else if(copied_rectangle.getStrokeDashArray().size() == 2){
                copied_style = 2;
            } else if(copied_rectangle.getStrokeDashArray().size() == 4){
                copied_style = 3;
            }
            copied_line_width = selected_rectangle.getStrokeWidth();
        } else if(selected_shape == "circle"){
            copied_shape = "circle";
            copied_width = selected_circle.getRadius();
            copied_height = selected_circle.getRadius();
            copied_fill_color = (Color)selected_circle.getFill();
            copied_line_color = (Color)selected_circle.getStroke();
            if(copied_circle.getStrokeDashArray().size() == 0){
                copied_style = 1;
            } else if(copied_circle.getStrokeDashArray().size() == 2){
                copied_style = 2;
            } else if(copied_circle.getStrokeDashArray().size() == 4){
                copied_style = 3;
            }
            copied_line_width = selected_circle.getStrokeWidth();
        } else if(selected_shape == "line"){
            copied_shape = "line";
            copied_startx = selected_line.getStartX();
            copied_starty = selected_line.getStartY();
            copied_endx = selected_line.getEndX();
            copied_endy = selected_line.getEndY();
            copied_line_color = (Color)selected_line.getStroke();
            if(copied_line.getStrokeDashArray().size() == 0){
                copied_style = 1;
            } else if(copied_line.getStrokeDashArray().size() == 2){
                copied_style = 2;
            } else if(copied_line.getStrokeDashArray().size() == 4){
                copied_style = 3;
            }
            copied_line_width = selected_line.getStrokeWidth();
        }
    }

    public void paste(){
        System.out.println(copied_shape);
        if(selected_shape == "rectangle"){
            main_screen.getChildren().remove(selected_rectangle);
            selected_shape = "none";
        } else if(selected_shape == "circle"){
            main_screen.getChildren().remove(selected_circle);
            selected_shape = "none";
        } else if(selected_shape == "line"){
            main_screen.getChildren().remove(selected_line);
            selected_shape = "none";
        }

        if(copied_shape == "rectangle"){
            Rectangle r = new Rectangle();
            r.setX(500);
            r.setY(500);
            r.setWidth(copied_width);
            r.setHeight(copied_height);
            r.setFill(copied_fill_color);
            r.setStroke(copied_line_color);
            if(copied_style == 1){
                r.getStrokeDashArray().setAll();
            } else if(copied_style == 2){
                r.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
            } else if(copied_style == 3){
                r.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
            }
            r.setStrokeWidth(copied_line_width);
            rect_mouse_events(r);
            select_rect(r);
            main_screen.getChildren().add(r);
        } else if(copied_shape == "circle"){
            Circle c = new Circle();
            c.setCenterX(500);
            c.setCenterY(500);
            c.setRadius(copied_width);
            c.setFill(copied_fill_color);
            c.setStroke(copied_line_color);
            if(copied_style == 1){
                c.getStrokeDashArray().setAll();
            } else if(copied_style == 2){
                c.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
            } else if(copied_style == 3){
                c.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
            }
            c.setStrokeWidth(copied_line_width);
            circle_mouse_events(c);
            select_circle(c);
            main_screen.getChildren().add(c);
        }  else if(copied_shape == "line"){
            Line l = new Line();
            l.setStartX(500);
            l.setStartY(500);
            l.setEndX(500 + copied_endx - copied_startx);
            l.setEndY(500 + copied_endy - copied_starty);
//            copied_startx
//                    copied_starty
//            copied_endx
//                    copied_endy
            l.setFill(copied_fill_color);
            l.setStroke(copied_line_color);
            if(copied_style == 1){
                l.getStrokeDashArray().setAll();
            } else if(copied_style == 2){
                l.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
            } else if(copied_style == 3){
                l.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
            }
            l.setStrokeWidth(copied_line_width);
            line_mouse_events(l);
            select_line(l);
            main_screen.getChildren().add(l);
        }

    }


    public void create_rect(Rectangle r, double x_init, double y_init, double x_final, double y_final) {
        if (x_init > x_final) {
            if (y_init > y_final) {
                r.setX(x_final);
                r.setY(y_final);
            } else {
                r.setX(x_final);
                r.setY(y_init);
            }
        } else {
            if (y_init > y_final) {
                r.setX(x_init);
                r.setY(y_final);
            } else {
                r.setX(x_init);
                r.setY(y_init);
            }
        }
        r.setWidth(Math.abs(x_init - x_final));
        r.setHeight(Math.abs(y_init - y_final));
        r.setFill(fillColor);
        r.setStroke(lineColor);

        if(style_num == 1){
            r.getStrokeDashArray().setAll();
        } else if(style_num == 2){
            r.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
        } else if(style_num == 3){
            r.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
        }
        r.setStrokeWidth(thickness);
    }

    public void create_circle(Circle c, double x_init, double y_init, double x_final, double y_final) {
        c.setCenterX(x_init);
        c.setCenterY(y_init);
        c.setRadius(Math.sqrt(Math.pow(Math.abs(x_init - x_final), 2) + Math.pow(Math.abs(y_init - y_final), 2)));
        c.setFill(fillColor);
        c.setStroke(lineColor);
        if(style_num == 1){
            c.getStrokeDashArray().setAll();
        } else if(style_num == 2){
            c.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
        } else if(style_num == 3){
            c.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
        }
        c.setStrokeWidth(thickness);
    }

    public void create_line(Line l, double x_init, double y_init, double x_final, double y_final) {
        l.setStartX(x_init);
        l.setStartY(y_init);
        l.setEndX(x_final);
        l.setEndY(y_final);
        l.setStroke(lineColor);
        if(style_num == 1){
            l.getStrokeDashArray().setAll();
        } else if(style_num == 2){
            l.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
        } else if(style_num == 3){
            l.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
        }
        l.setStrokeWidth(thickness);
    }
    public void enable_colorpicker_buttons(){
        line_colorPicker.setDisable(false);
        fill_colorPicker.setDisable(false);
    }
    public void disable_colorpicker_buttons(){
        line_colorPicker.setDisable(true);
        fill_colorPicker.setDisable(true);
    }

    public void disable_thick_buttons(){
        thickness_button1.setDisable(true);
        thickness_button2.setDisable(true);
        thickness_button3.setDisable(true);
        style_button1.setDisable(true);
        style_button2.setDisable(true);
        style_button3.setDisable(true);


    }

    public void enable_thick_buttons(){
        thickness_button1.setDisable(false);
        thickness_button2.setDisable(false);
        thickness_button3.setDisable(false);
        style_button1.setDisable(false);
        style_button2.setDisable(false);
        style_button3.setDisable(false);
    }

    public void deselect_shape() {
        line_colorPicker.setValue(null);
        fill_colorPicker.setValue(null);
        text_line.setFill(null);
        text_fill.setFill(null);
        fillColor = null;
        lineColor = null;
        // deselect line thickness and style too
        if (selected_shape == "rectangle") {
            selected_rectangle.setEffect(null);
//            selected_rectangle.setStrokeWidth(thickness);
        } else if (selected_shape == "circle") {
            selected_circle.setEffect(null);
//            selected_circle.setStrokeWidth(thickness);
        } else if (selected_shape == "line") {
            selected_line.setEffect(null);
//            selected_line.setStrokeWidth(thickness);
        }
        selected_shape = "none";
    }

    public void select_rect(Rectangle r) {
        deselect_shape();
        enable_thick_buttons();
        enable_colorpicker_buttons();
        line_colorPicker.setValue((Color) r.getStroke());
        fill_colorPicker.setValue((Color) r.getFill());
        text_line.setFill(line_colorPicker.getValue());
        text_fill.setFill(fill_colorPicker.getValue());
        thickness = (int) r.getStrokeWidth();
        set_button_default(thickness_button1, thickness_button2, thickness_button3, style_button1, style_button2, style_button3);
        if(thickness == thick_1){
            thickness_button1.setStyle("-fx-background-color: #00ff00");
        } else if(thickness == thick_2){
            thickness_button2.setStyle("-fx-background-color: #00ff00");
        } else if(thickness == thick_3){
            thickness_button3.setStyle("-fx-background-color: #00ff00");
        }

        if(r.getStrokeDashArray().sorted().size() == style1_size){
            style_num = 1;
            style_button1.setStyle("-fx-background-color: #00ff00");
        } else if(r.getStrokeDashArray().sorted().size() == style2_size){
            style_num = 2;
            style_button2.setStyle("-fx-background-color: #00ff00");
        } else if(r.getStrokeDashArray().sorted().size() == style3_size){
            style_num = 3;
            style_button3.setStyle("-fx-background-color: #00ff00");
        }
        fillColor = (Color) r.getFill();
        lineColor = (Color) r.getStroke();
        selected_rectangle = r;
        selected_shape = "rectangle";

//        r.setStrokeType(null);
        DropShadow ds = new DropShadow();
        ds.setOffsetY(7);
        ds.setOffsetX(7);
        r.setEffect(ds);
    }

    public void select_circle(Circle c) {
        deselect_shape();
        enable_thick_buttons();
        enable_colorpicker_buttons();
        line_colorPicker.setValue((Color) c.getStroke());
        fill_colorPicker.setValue((Color) c.getFill());
        text_line.setFill(line_colorPicker.getValue());
        text_fill.setFill(fill_colorPicker.getValue());
        thickness = (int) c.getStrokeWidth();
        if(c.getStrokeDashArray().sorted().size() == style1_size){
            style_num = 1;
        } else if(c.getStrokeDashArray().sorted().size() == style2_size){
            style_num = 2;
        } else if(c.getStrokeDashArray().sorted().size() == style3_size){
            style_num = 3;
        }
        fillColor = (Color) c.getFill();
        lineColor = (Color) c.getStroke();
        selected_circle = c;
        selected_shape = "circle";
//        c.setStrokeWidth(thick_selected);
        DropShadow ds = new DropShadow();
        ds.setOffsetY(7);
        ds.setOffsetX(7);
        c.setEffect(ds);
    }


    public void select_line(Line l) {
        deselect_shape();
        enable_thick_buttons();
        enable_colorpicker_buttons();
        fill_colorPicker.setDisable(true);
//        fill_colorPicker.setValue((Color)c.getFill());
//        text_fill.setFill(fill_colorPicker.getValue());
//        fillColor = (Color)c.getFill();
        line_colorPicker.setValue((Color) l.getStroke());
        text_line.setFill(line_colorPicker.getValue());
        thickness = (int) l.getStrokeWidth();
        if(l.getStrokeDashArray().sorted().size() == style1_size){
            style_num = 1;
        } else if(l.getStrokeDashArray().sorted().size() == style2_size){
            style_num = 2;
        } else if(l.getStrokeDashArray().sorted().size() == style3_size){
            style_num = 3;
        }
        lineColor = (Color) l.getStroke();
        selected_line = l;
        selected_shape = "line";
//        l.setStrokeWidth(thick_selected);
        DropShadow ds = new DropShadow();
        ds.setOffsetY(7);
        ds.setOffsetX(7);
        l.setEffect(ds);
    }

    public void move_rectangle(Rectangle r, double x, double y) {
        double new_x = r.getX() + x;
        double new_y = r.getY() + y;
        r.setX(new_x);
        r.setY(new_y);
    }

    public void move_circle(Circle c, double x, double y) {
        double new_x = c.getCenterX() + x;
        double new_y = c.getCenterY() + y;
        c.setCenterX(new_x);
        c.setCenterY(new_y);
    }

    public void move_line(Line l, double x, double y) {
        l.setStartX(l.getStartX() + x);
        l.setStartY(l.getStartY() + y);
        l.setEndX(l.getEndX() + x);
        l.setEndY(l.getEndY() + y);
    }

//    public void scale_rect(Rectangle r, double init_x, double init_y, double x, double y){
//        double center_x;
//        double center_y;
//        if(x)
//    }

    public void rect_mouse_events(Rectangle r){
        r.setStyle("-fx-cursor: hand;");

        r.setOnMouseClicked(event -> {
            if (cur_tool == "selection") {
                //visual indication of selection
                select_rect((Rectangle) r);
                event.consume();
            } else if (cur_tool == "erase") {
                main_screen.getChildren().remove(r);
                selected_shape = "none";
                event.consume();
            } else if (cur_tool == "fill") {
                ((Rectangle) r).setFill(fillColor);
                event.consume();
            }
        });
        r.setOnDragDetected(event -> {
            if (cur_tool == "selection") {
                r.startFullDrag();
                event.consume();
            }
        });
        r.setOnMouseDragged(event -> {
            double my_x = event.getX();
            double my_y = event.getY();
            if(my_x > scene.getWidth()-158){
                my_x = scene.getWidth()-158;
            }
            if(my_y > scene.getHeight()-30){
                my_y = scene.getHeight()-30;
            }
            if(my_x < 0){
                my_x = 0;
            }
            if(my_y < 0){
                my_y = 0;
            }
            if (cur_tool == "selection") {
                if (dragged) move_rectangle((Rectangle) r, my_x - cur_x, my_y - cur_y);
                dragged = true;

                // scale here
//                        scale_rect((Rectangle)r, cur_x, cur_y, my_x, my_y);

                cur_x = my_x;
                cur_y = my_y;

                event.consume();
            }
        });
        r.setOnMouseDragExited(event -> {
            dragged = false;
        });
    }

    public void circle_mouse_events(Circle c){
        c.setStyle("-fx-cursor: hand;");


        c.setOnMouseClicked(event -> {
            if (cur_tool == "selection") {
                //visual indication of selection
                select_circle((Circle) c);
                event.consume();
            } else if (cur_tool == "erase") {
                main_screen.getChildren().remove(c);
                selected_shape = "none";
                event.consume();
            } else if (cur_tool == "fill") {
                ((Circle) c).setFill(fillColor);
                event.consume();
            }
        });
        c.setOnDragDetected(event -> {
            if (cur_tool == "selection") {
                c.startFullDrag();
                event.consume();
            }
        });
        c.setOnMouseDragged(event -> {
            double my_x = event.getX();
            double my_y = event.getY();
            if(my_x > scene.getWidth()-158){
                my_x = scene.getWidth()-158;
            }
            if(my_y > scene.getHeight()-30){
                my_y = scene.getHeight()-30;
            }
            if(my_x < 0){
                my_x = 0;
            }
            if(my_y < 0){
                my_y = 0;
            }
            if (cur_tool == "selection") {
                if (dragged) move_circle((Circle) c, my_x - cur_x, my_y - cur_y);
                dragged = true;

                // scale here

                cur_x = my_x;
                cur_y = my_y;
                event.consume();
            }
        });
        c.setOnMouseDragExited(event -> {
            dragged = false;
        });
    }
    public void line_mouse_events(Line l){
        l.setStyle("-fx-cursor: hand;");


        l.setOnMouseClicked(event -> {
            if (cur_tool == "selection") {
                //visual indication of selection
                select_line((Line) l);
                event.consume();
            } else if (cur_tool == "erase") {
                main_screen.getChildren().remove(l);
                selected_shape = "none";
                event.consume();
            } else if (cur_tool == "fill") {
                ((Line) l).setFill(fillColor);
                event.consume();
            }
        });
        l.setOnDragDetected(event -> {
            if (cur_tool == "selection") {
                l.startFullDrag();
            }
        });
        l.setOnMouseDragged(event -> {
            double my_x = event.getX();
            double my_y = event.getY();
            if(my_x > scene.getWidth()-158){
                my_x = scene.getWidth()-158;
            }
            if(my_y > scene.getHeight()-30){
                my_y = scene.getHeight()-30;
            }
            if(my_x < 0){
                my_x = 0;
            }
            if(my_y < 0){
                my_y = 0;
            }
            if (cur_tool == "selection") {
                if (dragged) move_line((Line) l, my_x - cur_x, my_y - cur_y);
                dragged = true;
                cur_x = my_x;
                cur_y = my_y;
                event.consume();
            }
        });
        l.setOnMouseDragExited(event -> {
            dragged = false;
        });
    }

    public void run_main_screen() {
        main_screen.setOnMouseClicked(e -> {
            System.out.println("mouse clicked");
            save_updated = false;
            if (was_dragged == true) {
                was_dragged = false;
            } else {
                if(cur_tool == "selection"){
                    disable_thick_buttons();
                    disable_colorpicker_buttons();
                }
                deselect_shape();
            }
            e.consume();
        });
        main_screen.setOnDragDetected(e -> {

            System.out.println("main_screen drag detected");
            main_screen.startFullDrag();
            e.consume();
        });

        main_screen.setOnMouseDragEntered(e -> {
            System.out.println("drag entered");
            if ((cur_tool == "rectangle" || cur_tool == "circle") && lineColor != null && fillColor != null) {
                cur_x = e.getX();
                cur_y = e.getY();
                if (cur_tool == "rectangle") {
                    main_screen.getChildren().add(cur_rectangle);
                } else if (cur_tool == "circle") {
                    main_screen.getChildren().add(cur_circle);
                }
            } else if (cur_tool == "line" && lineColor != null) {
                cur_x = e.getX();
                cur_y = e.getY();
                main_screen.getChildren().add(cur_line);
            }
            e.consume();
        });
        main_screen.setOnMouseDragged(e -> {
            double my_x = e.getX();
            double my_y = e.getY();
            if(my_x > scene.getWidth()-158){
                my_x = scene.getWidth()-158;
            }
            if(my_y > scene.getHeight()-30){
                my_y = scene.getHeight()-30;
            }
            if(my_x < 0){
                my_x = 0;
            }
            if(my_y < 0){
                my_y = 0;
            }

            if (cur_tool == "rectangle" && lineColor != null && fillColor != null) {
                create_rect(cur_rectangle, cur_x, cur_y, my_x, my_y);
            } else if (cur_tool == "circle" && lineColor != null && fillColor != null) {
                create_circle(cur_circle, cur_x, cur_y, my_x, my_y);
            } else if (cur_tool == "line" && lineColor != null) {
                create_line(cur_line, cur_x, cur_y, my_x, my_y);
            }
            was_dragged = true;
            e.consume();
        });

        main_screen.setOnMouseDragExited(e -> {
            double my_x_x = e.getX();
            double my_y_y = e.getY();
            if(my_x_x > scene.getWidth()-158){
                my_x_x = scene.getWidth()-158;
            }
            if(my_y_y > scene.getHeight()-30){
                my_y_y = scene.getHeight()-30;
            }
            if(my_x_x < 0){
                my_x_x = 0;
            }
            if(my_y_y < 0){
                my_y_y = 0;
            }
            if (cur_tool == "rectangle" && lineColor != null && fillColor != null) {
                Rectangle r = new Rectangle();
                create_rect(r, cur_x, cur_y, my_x_x, my_y_y);
                main_screen.getChildren().remove(cur_rectangle);
                rect_mouse_events(r);
                select_rect(r);
                main_screen.getChildren().add(r);
            } else if (cur_tool == "circle" && lineColor != null && fillColor != null) {
                Circle c = new Circle();
                create_circle(c, cur_x, cur_y, my_x_x, my_y_y);
                main_screen.getChildren().remove(cur_circle);
                circle_mouse_events(c);
                select_circle(c);
                main_screen.getChildren().add(c);
            } else if (cur_tool == "line" && lineColor != null) {
                Line l = new Line();
                create_line(l, cur_x, cur_y, my_x_x, my_y_y);
                main_screen.getChildren().remove(cur_line);
                line_mouse_events(l);
                select_line(l);
                main_screen.getChildren().add(l);
            }
            System.out.println("drag released");
            e.consume();
        });

    }

    public void setupLineThickness(VBox line_thickness_box) {
        line1_thickness.setStrokeWidth(thick_1);
        line2_thickness.setStrokeWidth(thick_2);
        line3_thickness.setStrokeWidth(thick_3);

        thickness_button1 = new Button("", line1_thickness);
        thickness_button2 = new Button("", line2_thickness);
        thickness_button3 = new Button("", line3_thickness);
        thickness_button1.setStyle("-fx-background-color: #00ff00");
        thickness_button1.setOnMouseClicked(mouseEvent -> {
            System.out.println("line 1");
            thickness = thick_1;
            if (selected_shape == "rectangle") {
                selected_rectangle.setStrokeWidth(thickness);
            } else if (selected_shape == "circle") {
                selected_circle.setStrokeWidth(thickness);
            } else if (selected_shape == "line") {
                selected_line.setStrokeWidth(thickness);
            }
            set_button_default_3(thickness_button1, thickness_button2, thickness_button3);
            thickness_button1.setStyle("-fx-background-color: #00ff00");
        });
        thickness_button2.setOnMouseClicked(mouseEvent -> {
            System.out.println("line 2");
            thickness = thick_2;
            if (selected_shape == "rectangle") {
                selected_rectangle.setStrokeWidth(thickness);
            } else if (selected_shape == "circle") {
                selected_circle.setStrokeWidth(thickness);
            } else if (selected_shape == "line") {
                selected_line.setStrokeWidth(thickness);
            }
            set_button_default_3(thickness_button1, thickness_button2, thickness_button3);
            thickness_button2.setStyle("-fx-background-color: #00ff00");
        });
        thickness_button3.setOnMouseClicked(mouseEvent -> {
            System.out.println("line 3");
            thickness = thick_3;
            if (selected_shape == "rectangle") {
                selected_rectangle.setStrokeWidth(thickness);
            } else if (selected_shape == "circle") {
                selected_circle.setStrokeWidth(thickness);
            } else if (selected_shape == "line") {
                selected_line.setStrokeWidth(thickness);
            }
            set_button_default_3(thickness_button1, thickness_button2, thickness_button3);
            thickness_button3.setStyle("-fx-background-color: #00ff00");
        });

        line_thickness_box.setSpacing(3);
        line_thickness_box.setPadding(new Insets(5, 10, 5, 10));
        line_thickness_box.getChildren().addAll(thickness_button1, thickness_button2, thickness_button3);
    }

    public void setupLineStyle(VBox line_style_box) {
        line1_style.getStrokeDashArray().setAll();
        line2_style.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
        line3_style.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);

        style_button1 = new Button("", line1_style);
        style_button2 = new Button("", line2_style);
        style_button3 = new Button("", line3_style);
        style_button1.setStyle("-fx-background-color: #00ff00");
        style_button1.setOnMouseClicked(mouseEvent -> {
            System.out.println("style 1");
            style_num = 1;
            if (selected_shape == "rectangle") {
                selected_rectangle.getStrokeDashArray().setAll();
            } else if (selected_shape == "circle") {
                selected_circle.getStrokeDashArray().setAll();
            } else if (selected_shape == "line") {
                selected_line.getStrokeDashArray().setAll();
            }
            set_button_default_3(style_button1, style_button2, style_button3);
            style_button1.setStyle("-fx-background-color: #00ff00");
        });
        style_button2.setOnMouseClicked(mouseEvent -> {
            System.out.println("style 2");
            style_num = 2;
            if (selected_shape == "rectangle") {
                selected_rectangle.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
            } else if (selected_shape == "circle") {
                selected_circle.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
            } else if (selected_shape == "line") {
                selected_line.getStrokeDashArray().setAll(style2_array[0], style2_array[1]);
            }
            set_button_default_3(style_button1, style_button2, style_button3);
            style_button2.setStyle("-fx-background-color: #00ff00");
        });
        style_button3.setOnMouseClicked(mouseEvent -> {
            System.out.println("style 3");
            style_num = 3;
            if (selected_shape == "rectangle") {
                selected_rectangle.getStrokeDashArray().setAll(style3_array[0], style3_array[1]);
            } else if (selected_shape == "circle") {
                selected_circle.getStrokeDashArray().setAll(style3_array[0], style3_array[1]);
            } else if (selected_shape == "line") {
                selected_line.getStrokeDashArray().setAll(style3_array[0], style3_array[1], style3_array[2], style3_array[3]);
            }
            set_button_default_3(style_button1, style_button2, style_button3);
            style_button3.setStyle("-fx-background-color: #00ff00");
        });

        line_style_box.setSpacing(3);
        line_style_box.setPadding(new Insets(5, 10, 5, 10));
        line_style_box.getChildren().addAll(style_button1, style_button2, style_button3);
    }

    // next two following functions were heavily based off of a sample given, the colorpickers
    //   were taken from samples/05.Widgets/04.color_picker
    public void setupLineColorPicker(VBox lineColorPicker_box) {
        line_colorPicker.setValue(lineColor);

        text_line.setFont(Font.font("Calibri", 20));
        text_line.setFill(line_colorPicker.getValue());
        line_colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                text_line.setFill(line_colorPicker.getValue());
                lineColor = line_colorPicker.getValue();
                if (selected_shape == "rectangle") {
                    selected_rectangle.setStroke(lineColor);
                } else if (selected_shape == "circle") {
                    selected_circle.setStroke(lineColor);
                } else if (selected_shape == "line") {
                    selected_line.setStroke(lineColor);
                }
            }
        });
        lineColorPicker_box.setPadding(new Insets(3, 10, 3, 10));
        lineColorPicker_box.getChildren().addAll(text_line, line_colorPicker);
    }


    public void setupFillColorPicker(VBox fillColorPicker_box) {
        fill_colorPicker.setValue(fillColor);

        text_fill.setFont(Font.font("Calibri", 20));
        text_fill.setFill(fill_colorPicker.getValue());
        fill_colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                text_fill.setFill(fill_colorPicker.getValue());
                fillColor = fill_colorPicker.getValue();
                if (selected_shape == "rectangle") {
                    selected_rectangle.setFill(fillColor);
                } else if (selected_shape == "circle") {
                    selected_circle.setFill(fillColor);
                } else if (selected_shape == "line") {
                    selected_line.setFill(fillColor);
                }
            }
        });
        fillColorPicker_box.setPadding(new Insets(3, 10, 3, 10));
        fillColorPicker_box.getChildren().addAll(text_fill, fill_colorPicker);
    }

    public void setupToolPalette(GridPane tool_palette) {
        ImageView selection_view = new ImageView(new Image("selection.png"));
        selection_view.setPreserveRatio(true);
        selection_view.setFitHeight(35);
        ImageView erase_view = new ImageView(new Image("eraser.png"));
        erase_view.setPreserveRatio(true);
        erase_view.setFitHeight(35);
        ImageView line_view = new ImageView(new Image("line.png"));
        line_view.setPreserveRatio(true);
        line_view.setFitHeight(35);
        ImageView circle_view = new ImageView(new Image("circle.png"));
        circle_view.setPreserveRatio(true);
        circle_view.setFitHeight(35);
        ImageView rectangle_view = new ImageView(new Image("rectangle.png"));
        rectangle_view.setPreserveRatio(true);
        rectangle_view.setFitHeight(35);
        ImageView fill_view = new ImageView(new Image("fill.png"));
        fill_view.setPreserveRatio(true);
        fill_view.setFitHeight(35);
        selection_tool = new Button("", selection_view);
        erase_tool = new Button("", erase_view);
        line_tool = new Button("", line_view);
        circle_tool = new Button("", circle_view);
        rectangle_tool = new Button("", rectangle_view);
        fill_tool = new Button("", fill_view);


        selection_tool.setOnAction(event -> {
            cur_tool = "selection";
            enable_colorpicker_buttons();
            enable_thick_buttons();
            set_button_default(selection_tool, erase_tool, line_tool, circle_tool, rectangle_tool, fill_tool);
            selection_tool.setStyle("-fx-background-color: #00ff00");
        });
        erase_tool.setOnAction(event -> {
            cur_tool = "erase";
            disable_colorpicker_buttons();
            disable_thick_buttons();
            set_button_default(selection_tool, erase_tool, line_tool, circle_tool, rectangle_tool, fill_tool);
            erase_tool.setStyle("-fx-background-color: #00ff00");
        });
        line_tool.setOnAction(event -> {
            cur_tool = "line";
            enable_colorpicker_buttons();
            enable_thick_buttons();
            fill_colorPicker.setDisable(true);
            set_button_default(selection_tool, erase_tool, line_tool, circle_tool, rectangle_tool, fill_tool);
            line_tool.setStyle("-fx-background-color: #00ff00");
        });
        circle_tool.setOnAction(event -> {
            cur_tool = "circle";
            enable_colorpicker_buttons();
            enable_thick_buttons();
            set_button_default(selection_tool, erase_tool, line_tool, circle_tool, rectangle_tool, fill_tool);
            circle_tool.setStyle("-fx-background-color: #00ff00");
        });
        rectangle_tool.setOnAction(event -> {
            cur_tool = "rectangle";
            enable_colorpicker_buttons();
            enable_thick_buttons();
            set_button_default(selection_tool, erase_tool, line_tool, circle_tool, rectangle_tool, fill_tool);
            rectangle_tool.setStyle("-fx-background-color: #00ff00");
        });
        fill_tool.setOnAction(event -> {
            cur_tool = "fill";
            enable_colorpicker_buttons();
            disable_thick_buttons();
            line_colorPicker.setDisable(true);
            set_button_default(selection_tool, erase_tool, line_tool, circle_tool, rectangle_tool, fill_tool);
            fill_tool.setStyle("-fx-background-color: #00ff00");
        });

        selection_tool.setPrefSize(50, 50);
        erase_tool.setPrefSize(50, 50);
        line_tool.setPrefSize(50, 50);
        circle_tool.setPrefSize(50, 50);
        rectangle_tool.setPrefSize(50, 50);
        fill_tool.setPrefSize(50, 50);

        tool_palette.add(selection_tool, 0, 0);
        tool_palette.add(line_tool, 0, 1);
        tool_palette.add(rectangle_tool, 0, 2);
        tool_palette.add(erase_tool, 1, 0);
        tool_palette.add(circle_tool, 1, 1);
        tool_palette.add(fill_tool, 1, 2);

        tool_palette.setAlignment(Pos.CENTER);
        tool_palette.setPadding(new Insets(5));
        tool_palette.setHgap(10);
        tool_palette.setVgap(10);
    }

    public void setupMenu(MenuBar menubar) {
        Menu menu_file = new Menu("File");
        MenuItem menu_new = new MenuItem("New");
        MenuItem menu_load = new MenuItem("Load");
        MenuItem menu_save = new MenuItem("Save");
        MenuItem menu_quit = new MenuItem("Quit");

        menu_new.setOnAction(e -> {
            System.out.println("Menu new clicked");
            if(!save_updated){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Save?");
                alert.setHeaderText("There are unsaved changes in the project");
                alert.setContentText("Would you like to save?");

                Optional<ButtonType> resultconfirm = alert.showAndWait();
                if (resultconfirm.get() == ButtonType.OK){
                    TextInputDialog dialogsave = new TextInputDialog(cur_save_file);
                    dialogsave.setTitle("Save");
                    dialogsave.setHeaderText("Save to file: (please enter with extension.txt)\nIf file exists, will be overwritten");
                    dialogsave.setContentText("File name:");
                    Optional<String> resultsave = dialogsave.showAndWait();
                    if (resultsave.isPresent()){
                        cur_save_file = resultsave.get();
                        save(resultsave.get());
                    }
                }
            }
            main_screen.getChildren().setAll();
        });
        menu_load.setOnAction(e -> {
            if(!save_updated){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Save?");
                alert.setHeaderText("There are unsaved changes in the project");
                alert.setContentText("Would you like to save?");

                Optional<ButtonType> resultconfirm = alert.showAndWait();
                if (resultconfirm.get() == ButtonType.OK){
                    TextInputDialog dialogsave = new TextInputDialog(cur_save_file);
                    dialogsave.setTitle("Save");
                    dialogsave.setHeaderText("Save to file: (please enter with extension.txt)\nIf file exists, will be overwritten");
                    dialogsave.setContentText("File name:");
                    Optional<String> resultsave = dialogsave.showAndWait();
                    if (resultsave.isPresent()){
                        cur_save_file = resultsave.get();
                        save(resultsave.get());
                    }
                }
            }
            TextInputDialog dialog = new TextInputDialog(cur_save_file);
            dialog.setTitle("Load");
            dialog.setHeaderText("Load file: (please enter with extension.txt)");
            dialog.setContentText("File name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                cur_save_file = result.get();
                load(result.get());
            }

            System.out.println("Menu load clicked");
        });
        menu_save.setOnAction(e -> {
            System.out.println("Menu save clicked");

            TextInputDialog dialog = new TextInputDialog(cur_save_file);
            dialog.setTitle("Save");
            dialog.setHeaderText("Save to file: (please enter with extension.txt)\nIf file exists, will be overwritten");
            dialog.setContentText("File name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                cur_save_file = result.get();
                save(result.get());
            }
        });
        menu_quit.setOnAction(e -> {
            if(!save_updated){
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Save?");
                alert.setHeaderText("There are unsaved changes in the project");
                alert.setContentText("Would you like to save?");

                Optional<ButtonType> resultconfirm = alert.showAndWait();
                if (resultconfirm.get() == ButtonType.OK){
                    TextInputDialog dialogsave = new TextInputDialog(cur_save_file);
                    dialogsave.setTitle("Save");
                    dialogsave.setHeaderText("Save to file: (please enter with extension.txt)\nIf file exists, will be overwritten");
                    dialogsave.setContentText("File name:");
                    Optional<String> resultsave = dialogsave.showAndWait();
                    if (resultsave.isPresent()){
                        cur_save_file = resultsave.get();
                        save(resultsave.get());
                    }
                }
            }
            Platform.exit();

            System.out.println("Menu quit clicked");
        });
        menu_file.getItems().add(menu_new);
        menu_file.getItems().add(menu_load);
        menu_file.getItems().add(menu_save);
        menu_file.getItems().add(menu_quit);

        Menu menu_help = new Menu("Help");
        MenuItem menu_about = new MenuItem("About");
        menu_about.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText(null);
            alert.setContentText("SketchIt by Lucas Chen, WatID: L443chen");
            alert.showAndWait();
        });
        menu_help.getItems().add(menu_about);

        Menu menu_edit = new Menu("Edit");
        MenuItem menu_cut = new MenuItem("Cut");
        MenuItem menu_copy = new MenuItem("Copy");
        MenuItem menu_paste = new MenuItem("Paste");
        menu_cut.setOnAction(e -> {
            if(selected_shape == "rectangle"){
                copy();
                main_screen.getChildren().remove(selected_rectangle);
                selected_shape = "none";
            } else if(selected_shape == "circle"){
                copy();
                main_screen.getChildren().remove(selected_circle);
                selected_shape = "none";
            } else if(selected_shape == "line"){
                copy();
                main_screen.getChildren().remove(selected_line);
                selected_shape = "none";
            }
        });
        menu_copy.setOnAction(e -> {
            if(selected_shape == "rectangle" || selected_shape == "circle" || selected_shape == "line"){
                copy();
            }
        });
        menu_paste.setOnAction(e -> {
            paste();
        });
        menu_edit.getItems().addAll(menu_cut, menu_copy, menu_paste);

        menubar.getMenus().add(menu_file);
        menubar.getMenus().add(menu_edit);
        menubar.getMenus().add(menu_help);

    }

    public void set_button_default(Button a, Button b, Button c, Button d, Button e, Button f) {
        a.setStyle(null);
        b.setStyle(null);
        c.setStyle(null);
        d.setStyle(null);
        e.setStyle(null);
        f.setStyle(null);
    }

    public void set_button_default_3(Button a, Button b, Button c) {
        a.setStyle(null);
        b.setStyle(null);
        c.setStyle(null);
    }
}
