/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author Szaman
 */
public class FXMLDocumentController
{
    @FXML
    private Label nameLabel;
    @FXML
    private Label combinationLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label minRangeLabel;
    @FXML
    private Label maxRangeLabel;
    @FXML
    private TextField pointsCount;
    @FXML
    private ListView xCoordsView;
    @FXML
    private ListView yCoordsView;
    @FXML
    private ListView ordinaryNumberView;
    @FXML
    private NumberAxis xAxisMin;
    @FXML
    private NumberAxis xAxisMax;
    @FXML
    private NumberAxis yAxisMin;
    @FXML
    private NumberAxis yAxisMax;
    @FXML
    private LineChart<Number,Number> minimumRangeChart;
    @FXML
    private LineChart<Number,Number> maximumRangeChart;
    
    @FXML
    private RadioButton DFSButton; //przeszukiwanie wglab
    @FXML
    private RadioButton BFSButton; //przeszukiwanie wszerz
    @FXML
    private RadioButton LCPButton; //najmniejszy koszt
    
    final ToggleGroup methodGroup = new ToggleGroup();
    
    @FXML
    private Label minimumTraceLabel;
    @FXML
    private Label maximumTraceLabel;
    
    private int pointsNumber = 0;
    
    static List<String> ordinaryNumberList = new ArrayList<String>();
    static List<Integer> coordXList = new ArrayList<Integer>();
    static List<Integer> coordYList = new ArrayList<Integer>();
    
    
        
    
    Set<Integer> points = new HashSet<>(0);
    
    private String selectedMethod = "";
    
    //skrypt dla guzika Start!
    @FXML
    private void generation() throws SQLException, ClassNotFoundException 
    {
        DFSButton.setUserData("DFS");
        DFSButton.setToggleGroup(methodGroup);
        
        BFSButton.setUserData("BFS");
        BFSButton.setToggleGroup(methodGroup);
        
        LCPButton.setUserData("LCP");
        LCPButton.setToggleGroup(methodGroup);
        
        if(DFSButton.isSelected() == true)
        {
            selectedMethod = methodGroup.getSelectedToggle().getUserData().toString();
        }
        
        if(BFSButton.isSelected() == true)
        {
            selectedMethod = methodGroup.getSelectedToggle().getUserData().toString();
        }
        
        if(LCPButton.isSelected() == true)
        {
            selectedMethod = methodGroup.getSelectedToggle().getUserData().toString();
        }
        
        methodGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) 
            {
                if (methodGroup.getSelectedToggle() != null) 
                {
                    selectedMethod = methodGroup.getSelectedToggle().getUserData().toString();
                }                
            }
        });
        
        
        ordinaryNumberView.setItems(null);
        xCoordsView.setItems(null);
        yCoordsView.setItems(null);
        combinationLabel.setText("");
        
        if (validator()) 
        {
            ordinaryNumberList.clear();
            coordXList.clear();
            coordYList.clear();
            
            ordinaryNumberList = new ArrayList();
            coordXList = new ArrayList();
            coordYList = new ArrayList();
            
            int factorial = 1;
            int combinationCount = 1;
            
            
            
            pointsNumber = Integer.parseInt(pointsCount.getText());
            Set<Integer> pointsForPermutation = new HashSet<>(0);
            System.out.println("Liczba:" + pointsNumber);
            Random generator = new Random();
            for (int i = 0; i < pointsNumber; i++)
            {
                int randomCoordX = generator.nextInt(11) + 1;
                int randomCoordY = generator.nextInt(11) + 1;
                ordinaryNumberList.add(i+1 + ")");
                pointsForPermutation.add(i+1);
                coordXList.add(randomCoordX);
                coordYList.add(randomCoordY);
            }
            
            pointsForPermutation.remove(1); //Poniewaz szukamy wszystkich mozliwych drog od pierwszego do ostatniego punktu (oba punkty sa niezmienne).
            
            for (int i = 0; i < pointsNumber; i++)
            {
                System.out.print(coordXList.get(i) + ",");
                System.out.println(coordYList.get(i));
            }
            if(pointsNumber<4)
            {
                factorial = 1;
            }
            else
            {
                combinationCount = 0;
                for(int i = 1; i <= pointsNumber - 1; i++)
                {
                    factorial = factorial * i;
                    combinationCount++;
                }
            }
            ObservableList<String> observableOrdinaryNumber = FXCollections.observableArrayList(ordinaryNumberList);
            ObservableList<Integer> observableX = FXCollections.observableArrayList(coordXList);
            ObservableList<Integer> observableY = FXCollections.observableArrayList(coordYList);
            
            ordinaryNumberView.setItems(observableOrdinaryNumber);
            xCoordsView.setItems(observableX);
            yCoordsView.setItems(observableY);
            
            
            List<String> generatedPermutations = permutationsGenerator(pointsForPermutation);
            
            System.out.println(generatedPermutations);
            
            combinationLabel.setText("Liczba kombinacji: " + combinationCount + "! (" + factorial + ").");
            
            System.out.println("Permutacje: " + pointsForPermutation);
                
            //List<Double> resultList = new ArrayList<>();
            
            if(selectedMethod.equals("DFS"))
            {
                long start = System.currentTimeMillis();
                
                List<Double> resultList = DFSResolver(generatedPermutations);
                
                long elapsedTime = System.currentTimeMillis()-start;
                
                nameLabel.setText("Wynik (DFS)");
                timeLabel.setText("Czas obliczeń: " + elapsedTime + "ms");
                minRangeLabel.setText("Najkrótsza droga: " + resultList.get(0));
                maxRangeLabel.setText("Najdłuższa droga: " + resultList.get(1));

                chartWriter(resultList.get(2).intValue(),resultList.get(3).intValue());
            }
            if(selectedMethod.equals("BFS"))
            {
                long start = System.currentTimeMillis();
                
                List<Double> resultList = BFSResolver(generatedPermutations);
                
                long elapsedTime = System.currentTimeMillis()-start;
                
                nameLabel.setText("Wynik (BFS)");
                timeLabel.setText("Czas obliczeń: " + elapsedTime + "ms");
                minRangeLabel.setText("Najkrótsza droga: " + resultList.get(0));
                maxRangeLabel.setText("Najdłuższa droga: " + resultList.get(1));
                
                chartWriter(resultList.get(2).intValue(),resultList.get(3).intValue());
            }
            if(selectedMethod.equals("LCP"))
            {
                long start = System.currentTimeMillis();
                
                List<Double> resultList = LCPResolver(generatedPermutations);
                
                long elapsedTime = System.currentTimeMillis()-start;
                
                nameLabel.setText("Wynik (LCP)");
                timeLabel.setText("Czas obliczeń: " + elapsedTime + "ms");
                minRangeLabel.setText("Najkrótsza droga: " + resultList.get(0));
                maxRangeLabel.setText("");
                
                chartWriter(resultList.get(1).intValue(),resultList.get(1).intValue());
            }
            else
            {
                nameLabel.setText("Proszę wybrać metodę!");
            }
        }
    }

    //Walidator danych.
    private boolean validator() throws SQLException, ClassNotFoundException 
    {
        String errorMessage = ""; //Pusty string, do ktorego beda dodawane poszczegolne errory.
                                
        if (pointsCount.getText() == null || pointsCount.getText().length() == 0) 
        {
            errorMessage += "Nie podano ilości punktów!" + "\n";
        }
            
        else
        { 
            Pattern noChars = Pattern.compile("^[0-9]*$"); //Wzorzec, w ktorym nie wystepuja litery ani znaki specjalne.
            Matcher matchPointsCount = noChars.matcher(pointsCount.getText()); //Matcher pobiera podana ilosc punktow porownuje, czy zgadza sie ze wzorcem.
            Boolean boolMatchPointsCount = matchPointsCount.matches(); //Logika dla matchera od ilosci punktow.

            //Ilosc punktow podana z literami.
            if (!(boolMatchPointsCount))
            {
                errorMessage += "Ilość punktów musi składać się tylko z cyfr!" + "\n";
            }

            //Nieodpowiedna ilosc punktow (za malo).
            if (Integer.parseInt(pointsCount.getText()) < 2) 
            {
                errorMessage += "Podano za małą ilość punktów!" + "\n";
            }

            //Nieodpowiedna ilosc punktow (za duzo).
            if (Integer.parseInt(pointsCount.getText()) > 9) 
            {
                errorMessage += "Podano za dużą ilość punktów!" + "\n";
            }
        }
       
        
    //Gdy liczba bledow jest rowna zero:
        if (errorMessage.length() == 0) 
        {
            return true;
        } 
        
    //Gdy liczba bledow jest rozna od zera:
        else 
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Proszę podać prawidłową liczbę punktów (2-9)!");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
    
    //generator permutacji
    public static List<String> permutationsGenerator(Set<Integer> pointsForPermutation) 
    {
        List<String> permutations = new LinkedList<>();

        for (Integer number : pointsForPermutation) 
        {
            Set<Integer> pointsSet = new HashSet<>(pointsForPermutation);
            pointsSet.remove(number);

            if (!pointsSet.isEmpty()) 
            {
                List<String> secondaryPermutationsList = permutationsGenerator(pointsSet);
                for (String secondaryPermutation : secondaryPermutationsList) 
                {
                    String permutation = number + secondaryPermutation;
                    permutations.add(permutation);
                }
            } 
            else 
            {
                permutations.add(number.toString());
            }
        }
        return permutations;
    }
    
    //Algorytm DFS.
    public static List<Double> DFSResolver(List<String> generatedPermutations)
    {
        List<Double> result = new LinkedList<>();
        double minimumRange = -1;
        double maximumRange = -1;
        String minimumRoute = "";
        String maximumRoute = "";
        double minimumRouteNumber = -1;
        double maximumRouteNumber = -1;
        
        if(coordXList.size()>2)
        {
            int permutationsCount = generatedPermutations.size();
            int rangePoints = generatedPermutations.get(0).length() + 2; //+2, bo liczymy odleglosc lacznie z punktem poczatkowym i docelowym.
            
            String fullRoute;
            
            for(int i = 0; i < permutationsCount; i++)
            {
                int firstPointX = -1;
                int firstPointY = -1;
                int secondPointX = -1;
                int secondPointY = -1;
                
                int horizontalRange;
                int verticalRange;
                
                double range;
                double overallRange = -1;

                fullRoute = "1" + generatedPermutations.get(i) + "1"; //Pelna trasa zaczynajac od punktu poczatkowego konczac na punkcie ostatnim.
                int[] splittedPoints = new int[rangePoints];
                
                for(int j = 0; j < rangePoints; j++)
                {
                    splittedPoints[j] = Character.getNumericValue(fullRoute.charAt(j));
                }
                
                for(int j = 1; j < rangePoints; j++)
                {
                    if(firstPointX == -1)
                    {
                        int whereToGoNow = splittedPoints[j] - 1;
                        firstPointX = coordXList.get(0);
                        firstPointY = coordYList.get(0);
                        
                        secondPointX = coordXList.get(whereToGoNow);
                        secondPointY = coordYList.get(whereToGoNow);
                        
                        horizontalRange = Math.abs(firstPointX - secondPointX);
                        verticalRange = Math.abs(firstPointY - secondPointY);
                        
                        range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));

                        overallRange = range;
                    }
                    else
                    {
                        int whereToGoNow = splittedPoints[j] - 1;
                        firstPointX = secondPointX;
                        firstPointY = secondPointY;
                        
                        secondPointX = coordXList.get(whereToGoNow);
                        secondPointY = coordYList.get(whereToGoNow);
                        
                        horizontalRange = Math.abs(firstPointX - secondPointX);
                        verticalRange = Math.abs(firstPointY - secondPointY);
                        
                        range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));

                        overallRange = overallRange + range;
                    }
                }
                if(i==0)
                {
                    minimumRange = overallRange;
                    maximumRange = overallRange;
                    
                    minimumRouteNumber = Double.parseDouble(fullRoute);
                    maximumRouteNumber = Double.parseDouble(fullRoute);
                }
                else
                {
                    if(overallRange < minimumRange)
                    {
                        minimumRange = overallRange;
                        minimumRouteNumber = Double.parseDouble(fullRoute);
                    }
                    if(overallRange > maximumRange)
                    {
                        maximumRange = overallRange;
                        maximumRouteNumber = Double.parseDouble(fullRoute);
                    }
                }
            }
        }
        else
        {
            int firstPointX;
            int firstPointY;
            int secondPointX;
            int secondPointY;
            
            int horizontalRange;
            int verticalRange;
            
            double range;
            double overallRange;
            
            firstPointX = coordXList.get(0);
            firstPointY = coordYList.get(0);
            
            secondPointX = coordXList.get(1);
            secondPointY = coordYList.get(1);
            
            horizontalRange = Math.abs(firstPointX - secondPointX);
            verticalRange = Math.abs(firstPointY - secondPointY);
            range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));            

            overallRange = range * 2;
            minimumRange = overallRange;
            maximumRange = overallRange;
            
            minimumRouteNumber = 121;
            maximumRouteNumber = 121;
        }
        result.add(minimumRange);
        result.add(maximumRange);
        result.add(minimumRouteNumber);
        result.add(maximumRouteNumber);
        return result;
    }
    
    //Algorytm BFS
    public static List<Double> BFSResolver(List<String> generatedPermutations)
    {
        List<Double> result = new LinkedList<>();
        List<String> allRouteList = new LinkedList<>();
        List<Double> allRangeList = new LinkedList<>();
        
        double minimumRange = -1;
        double maximumRange = -1;
        double minimumRouteNumber = -1;
        double maximumRouteNumber = -1;
        
        if(coordXList.size()>2)
        {
            int permutationsCount = generatedPermutations.size();
            int rangeSections = generatedPermutations.get(0).length() + 1; //+1, bo liczymy odcinki miedzy poszczegolnymi punktami + powrot
            
            String fullRoute;
            String actualRoute;
            
            for(int i = 0; i < permutationsCount; i++)
            {
                fullRoute = "1" + generatedPermutations.get(i) + "1"; //Pelna trasa zaczynajac od punktu poczatkowego konczac na punkcie ostatnim.
                allRouteList.add(fullRoute);
            }
            
            int firstPointX;
            int firstPointY;
            int secondPointX;
            int secondPointY;

            int horizontalRange;
            int verticalRange;

            double range;
            
            for(int i = 0; i < rangeSections; i++)
            {
                
                for(int j = 0; j < permutationsCount; j++)
                {
                    if(i == 0)
                    {
                        actualRoute = allRouteList.get(j);
                        int whereToGoNow = Integer.parseInt(actualRoute.charAt(i + 1) + "");
                        firstPointX = coordXList.get(0);
                        firstPointY = coordYList.get(0);
                        
                        secondPointX = coordXList.get(whereToGoNow - 1);
                        secondPointY = coordYList.get(whereToGoNow - 1);
                        
                        horizontalRange = Math.abs(firstPointX - secondPointX);
                        verticalRange = Math.abs(firstPointY - secondPointY);
                        
                        range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));

                        allRangeList.add(range);
                    }
                    else
                    {
                        actualRoute = allRouteList.get(j);
                        int whereToGoNow = Integer.parseInt(actualRoute.charAt(i) + "");
                        firstPointX = coordXList.get(whereToGoNow - 1);
                        firstPointY = coordYList.get(whereToGoNow - 1);
                        
                        whereToGoNow = Integer.parseInt(actualRoute.charAt(i + 1) + "");
                        
                        secondPointX = coordXList.get(whereToGoNow - 1);
                        secondPointY = coordYList.get(whereToGoNow - 1);
                        
                        horizontalRange = Math.abs(firstPointX - secondPointX);
                        verticalRange = Math.abs(firstPointY - secondPointY);
                        
                        range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));
                        double actualRangeValue = allRangeList.get(j) + range;
                        allRangeList.set(j, actualRangeValue);
                    }
                }
            }
            for(int i = 0; i < permutationsCount; i++)
            {
                if(i==0)
                {
                    minimumRange = allRangeList.get(i);
                    maximumRange = allRangeList.get(i);
                    
                    minimumRouteNumber = Double.parseDouble(allRouteList.get(i));
                    maximumRouteNumber = Double.parseDouble(allRouteList.get(i));
                }
                else
                {
                    if(allRangeList.get(i) < minimumRange)
                    {
                        minimumRange = allRangeList.get(i);
                        minimumRouteNumber = Double.parseDouble(allRouteList.get(i));
                    }
                    if(allRangeList.get(i) > maximumRange)
                    {
                        maximumRange = allRangeList.get(i);
                        maximumRouteNumber = Double.parseDouble(allRouteList.get(i));
                    }
                }
            }
        }
        else
        {
            int firstPointX;
            int firstPointY;
            int secondPointX;
            int secondPointY;
            
            int horizontalRange;
            int verticalRange;
            
            double range;
            double overallRange;
            
            firstPointX = coordXList.get(0);
            firstPointY = coordYList.get(0);
            
            secondPointX = coordXList.get(1);
            secondPointY = coordYList.get(1);
            
            horizontalRange = Math.abs(firstPointX - secondPointX);
            verticalRange = Math.abs(firstPointY - secondPointY);
            range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));            

            overallRange = range * 2;
            minimumRange = overallRange;
            maximumRange = overallRange;
            
            minimumRouteNumber = 121;
            maximumRouteNumber = 121;
        }
        result.add(minimumRange);
        result.add(maximumRange);
        result.add(minimumRouteNumber);
        result.add(maximumRouteNumber);
        return result;
    }
    
    //Algorytm LCP
    public static List<Double> LCPResolver(List<String> generatedPermutations)
    {
        List<Double> result = new LinkedList<>();
        
        double actualDistance = -1;
        double overallDistance = -1;
        
        String leastCostRoute = "1";
        
        if(coordXList.size()>2)
        {
            int pointsCount = coordXList.size();
            
            List<Integer> tempCoordXList = new ArrayList<Integer>(coordXList);
            List<Integer> tempCoordYList = new ArrayList<Integer>(coordYList);
            List<String> tempOrdinaryNumberList = new ArrayList<String>(ordinaryNumberList);
                        
            int firstPointX;
            int firstPointY;
            int secondPointX;
            int secondPointY;

            int horizontalRange;
            int verticalRange;

            double overallRange = 0;
            double leastActualRange = 0;
            double range = 0;
            
            int actualPoint = 0;
            
            for(int i = 0; i < pointsCount; i++)
            {
                if(i == 0)
                {
                    leastActualRange = 0;
                    for(int j = 1; j < tempOrdinaryNumberList.size(); j++)
                    {
                        String pointNumber = tempOrdinaryNumberList.get(j);
                        int whereToGoNow = Integer.parseInt(pointNumber.replaceAll("[^0-9]+", ""));
                        firstPointX = coordXList.get(0);
                        firstPointY = coordYList.get(0);
                        
                        secondPointX = coordXList.get(whereToGoNow - 1);
                        secondPointY = coordYList.get(whereToGoNow - 1);
                        
                        horizontalRange = Math.abs(firstPointX - secondPointX);
                        verticalRange = Math.abs(firstPointY - secondPointY);
                        
                        range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));
                        
                        if(leastActualRange == 0)
                        {
                            leastActualRange = range;
                            actualPoint = whereToGoNow;
                        }
                        else
                        {
                            if(range < leastActualRange)
                            {
                                leastActualRange = range;
                                actualPoint = whereToGoNow;
                            }
                        }
                    }
                    overallRange += leastActualRange;
                    leastCostRoute += actualPoint + "";
                }
                else
                {
                    if(i < pointsCount - 1)
                    {
                        leastActualRange = 0;
                        System.out.println("do usuniecia: " + actualPoint + " wielkosc listy: " + tempOrdinaryNumberList.size() + " droga: " + leastCostRoute);
                        tempOrdinaryNumberList.remove(actualPoint + ")");
                        
                        firstPointX = coordXList.get(actualPoint - 1);
                        firstPointY = coordYList.get(actualPoint - 1);
                        
                        for(int j = 1; j < tempOrdinaryNumberList.size(); j++)
                        {
                            String pointNumber = tempOrdinaryNumberList.get(j);
                            int whereToGoNow = Integer.parseInt(pointNumber.replaceAll("[^0-9]+", ""));
                            
                            secondPointX = coordXList.get(whereToGoNow - 1);
                            secondPointY = coordYList.get(whereToGoNow - 1);
                            
                            System.out.println(firstPointX + ", " + firstPointY + " do " + secondPointX + ", " + secondPointY);

                            horizontalRange = Math.abs(firstPointX - secondPointX);
                            verticalRange = Math.abs(firstPointY - secondPointY);

                            range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));

                            if(leastActualRange == 0)
                            {
                                leastActualRange = range;
                                actualPoint = whereToGoNow;
                            }
                            else
                            {
                                if(range < leastActualRange)
                                {
                                    leastActualRange = range;
                                    actualPoint = whereToGoNow;
                                }
                            }
                        }
                        overallRange += leastActualRange;
                        leastCostRoute += actualPoint + "";
                    }
                    else
                    {
                        firstPointX = coordXList.get(actualPoint - 1);
                        firstPointY = coordYList.get(actualPoint - 1);

                        secondPointX = coordXList.get(0);
                        secondPointY = coordYList.get(0);

                        horizontalRange = Math.abs(firstPointX - secondPointX);
                        verticalRange = Math.abs(firstPointY - secondPointY);

                        range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));
                        
                        overallRange += range;
                        leastCostRoute += 1 + "";
                    }
                }
            }
        }
        else
        {
            int firstPointX;
            int firstPointY;
            int secondPointX;
            int secondPointY;
            
            int horizontalRange;
            int verticalRange;
            
            double range;
            double overallRange;
            
            
            firstPointX = coordXList.get(0);
            firstPointY = coordYList.get(0);
            
            secondPointX = coordXList.get(1);
            secondPointY = coordYList.get(1);
            
            horizontalRange = Math.abs(firstPointX - secondPointX);
            verticalRange = Math.abs(firstPointY - secondPointY);
            range = Math.sqrt(Math.pow(horizontalRange, 2) + Math.pow(verticalRange, 2));            

            overallRange = range * 2;
            overallDistance = overallRange;
            
            leastCostRoute = "121";
        }
        result.add(overallDistance);
        result.add(Double.parseDouble(leastCostRoute));
        return result;
    }
    
    //szkicownik wykresow
    private void chartWriter(int minimumRoute, int maximumRoute)
    {
        List<Integer> pointsOfMinimumRoute = new ArrayList<Integer>();
        List<Integer> pointsOfMaximumRoute = new ArrayList<Integer>();
        
        minimumTraceLabel.setText("");
        maximumTraceLabel.setText("");
        
        minimumRangeChart.getData().clear();
        maximumRangeChart.getData().clear();
        
        String minimumTraceInfo = "Najkrótsza droga: ";
        String maximumTraceInfo = "Najdłuższa droga: ";
        
        String number = String.valueOf(minimumRoute);
        
        int range = number.length();
        for(int i = 0; i < range; i++) 
        {
            int digit = Character.digit(number.charAt(i), 10);
            pointsOfMinimumRoute.add(digit);
            minimumTraceInfo = minimumTraceInfo + digit + " ";
        }
                
        number = String.valueOf(maximumRoute);
        for(int i = 0; i < range; i++) 
        {
            int digit = Character.digit(number.charAt(i), 10);
            pointsOfMaximumRoute.add(digit);
            maximumTraceInfo = maximumTraceInfo + digit + " ";
        }
        
        minimumTraceLabel.setText(minimumTraceInfo);
        maximumTraceLabel.setText(maximumTraceInfo);
        
        XYChart.Series traceForMinimumRoute = new XYChart.Series<>();
        XYChart.Series traceForMaximumRoute = new XYChart.Series<>();
        XYChart.Series startingPointForMinimumRoute = new XYChart.Series<>();
        XYChart.Series startingPointForMaximumRoute = new XYChart.Series<>();
        
        for(int i = 0; i < range; i++)
        {
           if(i == 0)
            {
                startingPointForMinimumRoute.getData().add(new XYChart.Data<>(coordXList.get(pointsOfMinimumRoute.get(i) - 1), coordYList.get(pointsOfMinimumRoute.get(i) - 1)));
                startingPointForMaximumRoute.getData().add(new XYChart.Data<>(coordXList.get(pointsOfMaximumRoute.get(i) - 1), coordYList.get(pointsOfMaximumRoute.get(i) - 1)));
           }
            
            traceForMinimumRoute.getData().add(new XYChart.Data<>(coordXList.get(pointsOfMinimumRoute.get(i) - 1), coordYList.get(pointsOfMinimumRoute.get(i) - 1)));
            traceForMaximumRoute.getData().add(new XYChart.Data<>(coordXList.get(pointsOfMaximumRoute.get(i) - 1), coordYList.get(pointsOfMaximumRoute.get(i) - 1)));
        }
                
        minimumRangeChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        maximumRangeChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        
        minimumRangeChart.getStylesheets().add("minimumChartStyle.css");
        maximumRangeChart.getStylesheets().add("maximumChartStyle.css");

        
        minimumRangeChart.getData().addAll(traceForMinimumRoute, startingPointForMinimumRoute);
        maximumRangeChart.getData().addAll(traceForMaximumRoute, startingPointForMaximumRoute);
    }
    
    @FXML
    private void handleExit(ActionEvent event) 
    {
        System.out.println("Zamykanie programu...");
        System.exit(0);
    }
}
