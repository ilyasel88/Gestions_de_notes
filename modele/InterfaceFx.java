import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.FileChooser;
import javafx.animation.*;
import javafx.util.Duration;
import java.io.*;
import java.util.*;
import javafx.scene.Node;

public class InterfaceFx extends Application {
    private StockageMySQL db;
    private String roleActuel;
    private String nomUtilisateur;
    private Etudiant etudiantConnecte;
    
    private Stage primaryStage;
    private BorderPane root;
    // Indicateur de modification
    private Label lampeIndicateur;
    private boolean hasUnsavedChanges = false;
    private String filiereResponsable;
    
    //  COULEURS THEME DARK PREMIUM 
    private static final String BG_PRIMARY = "#0F0F17";
    private static final String BG_SECONDARY = "#161622";
    private static final String BG_CARD = "#1E1E2E";
    private static final String BG_CARD_HOVER = "#252538";
    private static final String COLOR_ACCENT = "#6C63FF";
    private static final String COLOR_ACCENT_LIGHT = "#9D97FF";
    private static final String COLOR_ACCENT_DARK = "#4834d4";
    private static final String COLOR_SUCCESS = "#00E676";
    private static final String COLOR_DANGER = "#FF5252";
    private static final String COLOR_WARNING = "#FFD740";
    private static final String TEXT_PRIMARY = "#E8E8F5";
    private static final String TEXT_SECONDARY = "#8888A0";
    private static final String BG_INPUT = "#14141F";
    private static final String BORDER_COLOR = "#2D2D3D";
    private static final String SIDEBAR_BG = "#12121E";
    private static final String GLOW_COLOR = "#6C63FF33";
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        db = new StockageMySQL();
        primaryStage.setTitle("✦ Gestion des Notes — Académie");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(850);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(650);
        afficherConnexion();
    }
    
    //  MÉTHODE UTILITAIRE POUR COMBOBOX STYLISÉ 
    private <T> ComboBox<T> createStyledComboBox(ObservableList<T> items) {
        ComboBox<T> comboBox = new ComboBox<>(items);
        comboBox.setStyle("-fx-background-color: " + BG_CARD + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-padding: 8 12 8 12;");
        
        comboBox.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-background-color: " + BG_CARD + ";");
                }
            }
        });
        
        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
                }
            }
        });
        
        return comboBox;
    }

    private ObservableList<Promotion> getPromotionsFiltrees() {
    List<Promotion> list = db.getAllPromotions();
    if (roleActuel.equals("RESPONSABLE_FILIERE") && filiereResponsable != null) {
        list.removeIf(p -> !p.getFiliere().equals(filiereResponsable));
    }
    return FXCollections.observableArrayList(list);
}
    
    //  FENÊTRE DE CONNEXION 
    private void afficherConnexion() {
        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(450);
        card.setStyle("-fx-background-color: " + BG_CARD + ";" +
                      "-fx-background-radius: 15;" +
                      "-fx-border-color: #6C63FF33;" +
                      "-fx-border-radius: 15;" +
                      "-fx-border-width: 1;");
        card.setPadding(new Insets(40, 50, 40, 50));
        
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(GLOW_COLOR));
        glow.setRadius(20);
        glow.setSpread(0.1);
        card.setEffect(glow);
        
        Label titleLabel = new Label("🎓 GESTION DES NOTES");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web(COLOR_ACCENT));
        
        Label subLabel = new Label("Académie d'Éducation");
        subLabel.setFont(Font.font("Segoe UI", 14));
        subLabel.setTextFill(Color.web(TEXT_SECONDARY));
        
        TextField champNom = new TextField();
        champNom.setPromptText("Nom d'utilisateur");
        champNom.setStyle("-fx-background-color: " + BG_SECONDARY + ";" +
                          "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                          "-fx-prompt-text-fill: " + TEXT_SECONDARY + ";" +
                          "-fx-background-radius: 8;" +
                          "-fx-padding: 12 15 12 15;");
        
        PasswordField champMotDePasse = new PasswordField();
        champMotDePasse.setPromptText("Mot de passe");
        champMotDePasse.setStyle("-fx-background-color: " + BG_SECONDARY + ";" +
                                 "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                                 "-fx-prompt-text-fill: " + TEXT_SECONDARY + ";" +
                                 "-fx-background-radius: 8;" +
                                 "-fx-padding: 12 15 12 15;");
        
        Button btnConnexion = new Button("🔓 SE CONNECTER");
        btnConnexion.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_ACCENT + ", " + COLOR_ACCENT_DARK + ");" +
                              "-fx-text-fill: white;" +
                              "-fx-font-weight: bold;" +
                              "-fx-background-radius: 8;" +
                              "-fx-padding: 12 25 12 25;");
        btnConnexion.setOnMouseEntered(e -> btnConnexion.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_ACCENT_LIGHT + ", " + COLOR_ACCENT + ");-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 8;-fx-padding: 12 25 12 25;"));
        btnConnexion.setOnMouseExited(e -> btnConnexion.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_ACCENT + ", " + COLOR_ACCENT_DARK + ");-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 8;-fx-padding: 12 25 12 25;"));
        
        VBox formBox = new VBox(15, champNom, champMotDePasse, btnConnexion);
        card.getChildren().addAll(titleLabel, subLabel, formBox);
        
        HBox centerBox = new HBox(card);
        centerBox.setAlignment(Pos.CENTER);
        mainPane.setCenter(centerBox);
        
        Scene scene = new Scene(mainPane);
        String cssPath = getClass().getResource("/style.css") != null ? getClass().getResource("/style.css").toExternalForm() : new File("modele/style.css").toURI().toString();
        scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        btnConnexion.setOnAction(e -> {
            String nom = champNom.getText().trim();
            String motDePasse = champMotDePasse.getText();
            
            if (nom.isEmpty() || motDePasse.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }
            
            Utilisateur utilisateur = db.authentifier(nom, motDePasse);
            if (utilisateur == null) {
                showAlert("Erreur", "Nom ou mot de passe incorrect", Alert.AlertType.ERROR);
                return;
            }
            
            roleActuel = utilisateur.getRole();
            nomUtilisateur = nom;
            filiereResponsable = utilisateur.getFiliere();
            
            if (roleActuel.equals("ETUDIANT")) {
                etudiantConnecte = db.getEtudiantParNom(nom);
                if (etudiantConnecte == null) {
                    showAlert("Erreur", "Étudiant non trouvé", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            initInterface();
        });
    }
    
    //  INTERFACE PRINCIPALE 
    private void initInterface() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        
        VBox sidebar = new VBox(15);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, " + BG_SECONDARY + ", " + SIDEBAR_BG + ");" +
                         "-fx-border-color: transparent " + BORDER_COLOR + " transparent transparent;" +
                         "-fx-border-width: 0 1 0 0;");
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(260);
        
        VBox userBadge = new VBox(5);
        userBadge.setAlignment(Pos.CENTER);
        userBadge.setStyle("-fx-background-color: " + BG_INPUT + ";-fx-background-radius: 12;-fx-padding: 15;");
        
        Circle avatarBg = new Circle(25, Color.web(COLOR_ACCENT_DARK));
        Label avatarIcon = new Label("👤");
        avatarIcon.setFont(Font.font(24));
        avatarIcon.setTextFill(Color.WHITE);
        StackPane avatar = new StackPane(avatarBg, avatarIcon);
        
        Label userLabel = new Label(nomUtilisateur);
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        userLabel.setTextFill(Color.web(TEXT_PRIMARY));
        userLabel.setWrapText(true);
        userLabel.setAlignment(Pos.CENTER);
        
        Label roleLabel = new Label(getRoleText(roleActuel));
        roleLabel.setFont(Font.font("Segoe UI", 12));
        roleLabel.setTextFill(Color.web(COLOR_ACCENT_LIGHT));
        roleLabel.setAlignment(Pos.CENTER);
        
        userBadge.getChildren().addAll(avatar, userLabel, roleLabel);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button btnLogout = createSidebarButton("🔓 Déconnexion", COLOR_DANGER);
        btnLogout.setOnAction(e -> afficherConnexion());
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 0, 10, 0));
        
        sidebar.getChildren().addAll(userBadge, sep, getMenuButtons());
        sidebar.getChildren().addAll(spacer, btnLogout);
        
        root.setLeft(sidebar);
        
        StackPane centerPane = new StackPane();
        centerPane.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        root.setCenter(centerPane);
        
        Scene scene = new Scene(root);
        String cssPath = getClass().getResource("/style.css") != null ? getClass().getResource("/style.css").toExternalForm() : new File("modele/style.css").toURI().toString();
        scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        if (roleActuel.equals("PLANNING") || roleActuel.equals("RESPONSABLE_FILIERE")) {
            afficherPanneau(centerPane, panneauEtudiants());
        } else if (roleActuel.equals("ENSEIGNANT")) {
            afficherPanneau(centerPane, panneauSaisieNotes());
        } else if (roleActuel.equals("ETUDIANT")) {
            afficherPanneau(centerPane, panneauNotesEtudiant());
        }
    }
    
    private VBox getMenuButtons() {
        VBox menu = new VBox(5);
        
        if (roleActuel.equals("PLANNING")) {
            menu.getChildren().add(createMenuButton("👨‍🎓 Étudiants", () -> afficherPanneau((StackPane) root.getCenter(), panneauEtudiants())));
            menu.getChildren().add(createMenuButton("📦 Archives", () -> afficherPanneau((StackPane) root.getCenter(), panneauEtudiantsArchives())));
            menu.getChildren().add(createMenuButton("👨‍🏫 Enseignants", () -> afficherPanneau((StackPane) root.getCenter(), panneauEnseignants())));
            menu.getChildren().add(createMenuButton("🏫 Promotions", () -> afficherPanneau((StackPane) root.getCenter(), panneauPromotions())));
            menu.getChildren().add(createMenuButton("📚 Modules", () -> afficherPanneau((StackPane) root.getCenter(), panneauModules())));
            menu.getChildren().add(createMenuButton("📝 Notes", () -> afficherPanneau((StackPane) root.getCenter(), panneauNotesClasse())));
            menu.getChildren().add(createMenuButton("📊 Statistiques", () -> afficherPanneau((StackPane) root.getCenter(), panneauStatistiques())));
        } else if (roleActuel.equals("RESPONSABLE_FILIERE")) {
            menu.getChildren().add(createMenuButton("👨‍🎓 Étudiants", () -> afficherPanneau((StackPane) root.getCenter(), panneauEtudiants())));
            menu.getChildren().add(createMenuButton("📦 Archives", () -> afficherPanneau((StackPane) root.getCenter(), panneauEtudiantsArchives())));
            menu.getChildren().add(createMenuButton("✅ Validation Année", () -> afficherPanneau((StackPane) root.getCenter(), panneauValidationAnnee())));
            menu.getChildren().add(createMenuButton("📊 Statistiques", () -> afficherPanneau((StackPane) root.getCenter(), panneauStatistiques())));
        } else if (roleActuel.equals("ENSEIGNANT")) {
            menu.getChildren().add(createMenuButton("📝 Saisie notes", () -> afficherPanneau((StackPane) root.getCenter(), panneauSaisieNotes())));
        } else if (roleActuel.equals("ETUDIANT")) {
            menu.getChildren().add(createMenuButton("📖 Mes notes", () -> afficherPanneau((StackPane) root.getCenter(), panneauNotesEtudiant())));
            menu.getChildren().add(createMenuButton("📊 Ma progression", () -> afficherPanneau((StackPane) root.getCenter(), panneauProgressionEtudiant())));
        }
        
        return menu;
    }
    
    private Button createMenuButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent;-fx-text-fill: " + TEXT_SECONDARY + ";-fx-font-size: 14px;-fx-padding: 12 20 12 20;-fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + BG_CARD_HOVER + ";-fx-text-fill: " + COLOR_ACCENT_LIGHT + ";-fx-font-size: 14px;-fx-padding: 12 20 12 20;-fx-background-radius: 8;");
            btn.setTranslateX(5);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent;-fx-text-fill: " + TEXT_SECONDARY + ";-fx-font-size: 14px;-fx-padding: 12 20 12 20;-fx-background-radius: 8;");
            btn.setTranslateX(0);
        });
        btn.setOnAction(e -> action.run());
        return btn;
    }
    
    private Button createSidebarButton(String text, String color) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER);
        btn.setStyle("-fx-background-color: transparent;-fx-text-fill: " + color + ";-fx-font-weight: bold;-fx-padding: 12 15 12 15;-fx-background-radius: 8;-fx-border-color: " + color + "44;-fx-border-radius: 8;");
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + color + "22;-fx-text-fill: " + color + ";-fx-font-weight: bold;-fx-padding: 12 15 12 15;-fx-background-radius: 8;-fx-border-color: " + color + ";-fx-border-radius: 8;");
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent;-fx-text-fill: " + color + ";-fx-font-weight: bold;-fx-padding: 12 15 12 15;-fx-background-radius: 8;-fx-border-color: " + color + "44;-fx-border-radius: 8;");
        });
        return btn;
    }
    
    private void afficherPanneau(StackPane center, Node panneau) {
        center.getChildren().clear();
        panneau.setOpacity(0);
        panneau.setTranslateY(15);
        center.getChildren().add(panneau);
        
        FadeTransition ft = new FadeTransition(Duration.millis(300), panneau);
        ft.setToValue(1.0);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), panneau);
        tt.setToY(0);
        
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }
    
    private void applyThemeToDialog(DialogPane pane) {
        String cssPath = getClass().getResource("/style.css") != null ? getClass().getResource("/style.css").toExternalForm() : new File("modele/style.css").toURI().toString();
        if (!pane.getStylesheets().contains(cssPath)) {
            pane.getStylesheets().add(cssPath);
        }
        pane.setStyle("-fx-background-color: " + BG_CARD + ";");
    }
    
    private String getRoleText(String role) {
        switch (role) {
            case "PLANNING": return "📋 Responsable Planning & Évaluation";
            case "ENSEIGNANT": return "👨‍🏫 Enseignant";
            case "ETUDIANT": return "🎓 Étudiant";
            case "RESPONSABLE_FILIERE": return "📌 Responsable de Filière";
            default: return role;
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyThemeToDialog(alert.getDialogPane());
        alert.showAndWait();
    }
    private boolean isValidNumEtudiant(String num) {
    if (num == null || num.length() != 4) return false;
    char firstChar = num.charAt(0);
    if (firstChar < 'A' || firstChar > 'Z') return false;  // Uniquement A-Z majuscules
    for (int i = 1; i < 4; i++) {
        if (!Character.isDigit(num.charAt(i))) return false;
    }
    return true;
}

private boolean isValidDateNaissance(String date) {
    if (date == null || date.isEmpty()) return false;  // Plus optionnel, devient obligatoire
    if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) return false;
    
    String[] parts = date.split("/");
    int jour = Integer.parseInt(parts[0]);
    int mois = Integer.parseInt(parts[1]);
    int annee = Integer.parseInt(parts[2]);
    
    // Vérifier que l'année est cohérente (entre 1920 et 2008)
    if (annee < 1920 || annee > 2008) return false;
    
    // Vérifier le mois
    if (mois < 1 || mois > 12) return false;
    
    // Vérifier le jour selon le mois
    int maxJours;
    if (mois == 2) {
        boolean bissextile = (annee % 4 == 0 && annee % 100 != 0) || (annee % 400 == 0);
        maxJours = bissextile ? 29 : 28;
    } else if (mois == 4 || mois == 6 || mois == 9 || mois == 11) {
        maxJours = 30;
    } else {
        maxJours = 31;
    }
    
    return jour >= 1 && jour <= maxJours;
}

// Validation du téléphone marocain
private boolean isValidTelephone(String tel) {
    if (tel == null || tel.isEmpty()) return true; // Optionnel
    // Format: 06XXXXXXXX ou 07XXXXXXXX ou 05XXXXXXXX
    return tel.matches("^(06|07|05)\\d{8}$");
}

// Validation de l'email
private boolean isValidEmail(String email) {
    if (email == null || email.isEmpty()) return true; // Optionnel
    String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    return email.matches(regex);
}
    //  PANNEAU ÉTUDIANTS 
    private Node panneauEtudiants() {
        
        
        


        VBox panel = createStyledPanel("👨‍🎓 Gestion des étudiants");
        
        // Sélecteur de promotion
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER_LEFT);
        selectorBox.setPadding(new Insets(0, 0, 15, 0));
        
        Label selectPromoLabel = new Label("Sélectionner une promotion :");
        selectPromoLabel.setTextFill(Color.web(TEXT_PRIMARY));
        
        ComboBox<Promotion> comboPromo = createStyledComboBox(getPromotionsFiltrees());
        
        Button btnCharger = createButton("🔄 Charger les étudiants", COLOR_ACCENT);
        
        selectorBox.getChildren().addAll(selectPromoLabel, comboPromo, btnCharger);
        
        // Tableau des étudiants
        TableView<Etudiant> table = new TableView<>();
        
        
        TableColumn<Etudiant, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Etudiant, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPrenom.setPrefWidth(150);
        
        TableColumn<Etudiant, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(150);
        
        TableColumn<Etudiant, String> colNum = new TableColumn<>("Numéro");
        colNum.setCellValueFactory(new PropertyValueFactory<>("numEtudiant"));
        colNum.setPrefWidth(120);
        
        TableColumn<Etudiant, String> colArchive = new TableColumn<>("Archivé");
        colArchive.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isArchive() ? "✅ Oui" : "❌ Non"));
        colArchive.setPrefWidth(80);
        TableColumn<Etudiant, String> colDateNaiss = new TableColumn<>("Date naiss.");
        colDateNaiss.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colDateNaiss.setPrefWidth(100);

        TableColumn<Etudiant, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colTelephone.setPrefWidth(120);

        TableColumn<Etudiant, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(180);
        
        table.getColumns().addAll(colId, colPrenom, colNom, colNum, colDateNaiss, colTelephone, colEmail, colArchive);
        
        ObservableList<Etudiant> data = FXCollections.observableArrayList();
        table.setItems(data);
        
        // Boutons d'action
        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_LEFT);
        boutons.setPadding(new Insets(10, 0, 10, 0));
        
        Button btnAjouter = createButton("➕ Ajouter", COLOR_SUCCESS);
        Button btnModifier = createButton("✏️ Modifier", COLOR_WARNING);
        Button btnArchiver = createButton("📦 Archiver", COLOR_ACCENT);
        Button btnSupprimer = createButton("🗑️ Supprimer", COLOR_DANGER);
        
        boutons.getChildren().addAll(btnAjouter, btnModifier, btnArchiver, btnSupprimer);

        btnSupprimer.setOnAction(e -> {
            Etudiant selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Information", "Veuillez sélectionner un étudiant", Alert.AlertType.WARNING);
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Suppression définitive");
            confirm.setHeaderText("Suppression de l'étudiant");
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer DÉFINITIVEMENT " + selected.getPrenom() + " " + selected.getNom() + " ? Cela supprimera aussi son compte utilisateur, ses notes et ses validations.");
            applyThemeToDialog(confirm.getDialogPane());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    db.deleteEtudiantComplet(selected.getId());
                    Promotion p = comboPromo.getValue();
                    if (p != null) data.setAll(db.getEtudiantsByPromotion(p.getId()));
                    else data.setAll(db.getAllEtudiants());
                    showAlert("Succès", "Étudiant supprimé définitivement !", Alert.AlertType.INFORMATION);
                }
            });
        });

       btnModifier.setOnAction(e -> {
    Etudiant selected = table.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert("Information", "Veuillez sélectionner un étudiant", Alert.AlertType.WARNING);
        return;
    }
    
    // Stocker l'ancien numéro et l'ancienne date pour la mise à jour de l'utilisateur
    final String ancienNum = selected.getNumEtudiant();
    final String ancienneDate = selected.getDateNaissance();
    
    Dialog<Etudiant> dialog = new Dialog<>();
    dialog.setTitle("Modifier un étudiant");
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.initOwner(primaryStage);
    
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));
    
    TextField prenomField = new TextField(selected.getPrenom());
    TextField nomField = new TextField(selected.getNom());
    TextField numField = new TextField(selected.getNumEtudiant());
    numField.setPromptText("Format: A123");
    
    TextField dateField = new TextField(selected.getDateNaissance());
    dateField.setPromptText("DD/MM/YYYY");
    
    TextField telField = new TextField(selected.getTelephone());
    telField.setPromptText("06XXXXXXXX");
    
    TextField emailField = new TextField(selected.getEmail());
    emailField.setPromptText("exemple@domaine.com");
    
    Label prenomLbl = new Label("Prénom:");
    prenomLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(prenomLbl, 0, 0);
    grid.add(prenomField, 1, 0);
    
    Label nomLbl = new Label("Nom:");
    nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(nomLbl, 0, 1);
    grid.add(nomField, 1, 1);
    
    Label numLbl = new Label("Numéro étudiant:");
    numLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(numLbl, 0, 2);
    grid.add(numField, 1, 2);
    
    Label dateLbl = new Label("Date naissance:");
    dateLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(dateLbl, 0, 3);
    grid.add(dateField, 1, 3);
    
    Label telLbl = new Label("Téléphone:");
    telLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(telLbl, 0, 4);
    grid.add(telField, 1, 4);
    
    Label emailLbl = new Label("Email:");
    emailLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(emailLbl, 0, 5);
    grid.add(emailField, 1, 5);
    
    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    applyThemeToDialog(dialog.getDialogPane());
    
    dialog.setResultConverter(button -> {
        if (button == ButtonType.OK) {
            String numEtudiant = numField.getText().trim().toUpperCase();
            String dateNaiss = dateField.getText().trim();
            String telephone = telField.getText().trim();
            String email = emailField.getText().trim();
            
            if (!isValidNumEtudiant(numEtudiant)) {
                showAlert("Erreur", "Numéro invalide ! Format: 1 lettre + 3 chiffres", Alert.AlertType.ERROR);
                return null;
            }
            
            if (!isValidDateNaissance(dateNaiss)) {
                showAlert("Erreur", "Date de naissance obligatoire et invalide !\nFormat: DD/MM/YYYY (ex: 15/05/2000)", Alert.AlertType.ERROR);
                return null;
            }
            
            if (!isValidTelephone(telephone)) {
                showAlert("Erreur", "Téléphone invalide ! Format: 06XXXXXXXX ou 07XXXXXXXX", Alert.AlertType.ERROR);
                return null;
            }
            
            if (!isValidEmail(email)) {
                showAlert("Erreur", "Email invalide !", Alert.AlertType.ERROR);
                return null;
            }
            
            for (Etudiant existing : db.getAllEtudiants()) {
                if (existing.getId() != selected.getId() && existing.getNumEtudiant().equalsIgnoreCase(numEtudiant)) {
                    showAlert("Erreur", "Ce numéro existe déjà !", Alert.AlertType.ERROR);
                    return null;
                }
            }
            
            selected.setPrenom(prenomField.getText());
            selected.setNom(nomField.getText());
            selected.setNumEtudiant(numEtudiant);
            selected.setDateNaissance(dateNaiss);
            selected.setTelephone(telephone);
            selected.setEmail(email);
            return selected;
        }
        return null;
    });
    
    dialog.showAndWait().ifPresent(et -> {
        // Sauvegarder l'étudiant dans la base
        db.saveEtudiant(et);
        
        // Vérifier si le numéro ou la date a changé pour mettre à jour l'utilisateur
        boolean numChanged = !ancienNum.equals(et.getNumEtudiant());
        boolean dateChanged = !ancienneDate.equals(et.getDateNaissance());
        
        if (numChanged || dateChanged) {
            // Générer le nouveau mot de passe: nom + prenom + dateNaissance sans /
            String mdpUtilisateur = et.getNom().toLowerCase() + et.getPrenom().toLowerCase() + et.getDateNaissance().replace("/", "");
            
            // Si le numéro a changé, supprimer l'ancien utilisateur
            if (numChanged) {
                db.supprimerUtilisateur(ancienNum);
            }
            
            // Créer ou mettre à jour l'utilisateur
            db.creerUtilisateurEtudiant(et.getNumEtudiant(), mdpUtilisateur, et.getPrenom() + " " + et.getNom());
            
            showAlert("Succès", "✅ Étudiant modifié\n🔑 Identifiants mis à jour:\nUtilisateur: " + et.getNumEtudiant() + "\nMot de passe: " + mdpUtilisateur, Alert.AlertType.INFORMATION);
        } else {
            showAlert("Succès", "✅ Étudiant modifié avec succès !", Alert.AlertType.INFORMATION);
        }
        
        Promotion p = comboPromo.getValue();
        if (p != null) {
            data.setAll(db.getEtudiantsByPromotion(p.getId()));
        } else {
            data.setAll(db.getAllEtudiants());
        }
        table.refresh();
    });
});
        
        // Charger les étudiants de la promotion sélectionnée
        btnCharger.setOnAction(e -> {
            Promotion p = comboPromo.getValue();
            if (p == null) {
                showAlert("Information", "Veuillez sélectionner une promotion", Alert.AlertType.WARNING);
                return;
            }
            data.setAll(db.getEtudiantsByPromotion(p.getId()));
            if (data.isEmpty()) {
                showAlert("Information", "Aucun étudiant dans cette promotion", Alert.AlertType.INFORMATION);
            }
        });
        
        // Ajouter un étudiant (directement à la promotion sélectionnée)
        btnAjouter.setOnAction(e -> {
    Promotion p = comboPromo.getValue();
    if (p == null) {
        showAlert("Information", "Veuillez d'abord sélectionner une promotion", Alert.AlertType.WARNING);
        return;
    }
    
    Dialog<Etudiant> dialog = new Dialog<>();
    dialog.setTitle("Ajouter un étudiant à " + p.getNom());
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.initOwner(primaryStage);
    
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));
    
    TextField prenomField = new TextField();
    TextField nomField = new TextField();
    TextField numField = new TextField();
    numField.setPromptText("Format: A123");
    
    TextField dateField = new TextField();
    dateField.setPromptText("DD/MM/YYYY (ex: 15/05/2000)");
    
    TextField telField = new TextField();
    telField.setPromptText("06XXXXXXXX ou 07XXXXXXXX");
    
    TextField emailField = new TextField();
    emailField.setPromptText("exemple@domaine.com");
    
    Label prenomLbl = new Label("Prénom:");
    prenomLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(prenomLbl, 0, 0);
    grid.add(prenomField, 1, 0);
    
    Label nomLbl = new Label("Nom:");
    nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(nomLbl, 0, 1);
    grid.add(nomField, 1, 1);
    
    Label numLbl = new Label("Numéro étudiant:");
    numLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(numLbl, 0, 2);
    grid.add(numField, 1, 2);
    
    Label dateLbl = new Label("Date naissance:");
    dateLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(dateLbl, 0, 3);
    grid.add(dateField, 1, 3);
    
    Label telLbl = new Label("Téléphone:");
    telLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(telLbl, 0, 4);
    grid.add(telField, 1, 4);
    
    Label emailLbl = new Label("Email:");
    emailLbl.setTextFill(Color.web(TEXT_PRIMARY));
    grid.add(emailLbl, 0, 5);
    grid.add(emailField, 1, 5);
    
    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    applyThemeToDialog(dialog.getDialogPane());
    
    dialog.setResultConverter(button -> {
        if (button == ButtonType.OK) {
            String numEtudiant = numField.getText().trim().toUpperCase();
            String dateNaiss = dateField.getText().trim();
            String telephone = telField.getText().trim();
            String email = emailField.getText().trim();
            
            if (!isValidNumEtudiant(numEtudiant)) {
                showAlert("Erreur", "Numéro invalide ! Format: 1 lettre + 3 chiffres (ex: A123)", Alert.AlertType.ERROR);
                return null;
            }
            
            if (!isValidDateNaissance(dateNaiss)) {
                showAlert("Erreur", "Date de naissance obligatoire et invalide !\nFormat: DD/MM/YYYY (ex: 15/05/2000)", Alert.AlertType.ERROR);
                return null;
            }
            
            if (!isValidTelephone(telephone)) {
                showAlert("Erreur", "Téléphone invalide ! Format: 06XXXXXXXX ou 07XXXXXXXX", Alert.AlertType.ERROR);
                return null;
            }
            
            if (!isValidEmail(email)) {
                showAlert("Erreur", "Email invalide !", Alert.AlertType.ERROR);
                return null;
            }
            
            for (Etudiant existing : db.getAllEtudiants()) {
                if (existing.getNumEtudiant().equalsIgnoreCase(numEtudiant)) {
                    showAlert("Erreur", "Ce numéro existe déjà !", Alert.AlertType.ERROR);
                    return null;
                }
            }
            
            int newId = (int) (System.currentTimeMillis() % 10000);
            Etudiant et = new Etudiant(newId, prenomField.getText(), nomField.getText(), numEtudiant);
            et.setPromotion(p);
            et.setDateNaissance(dateNaiss);
            et.setTelephone(telephone);
            et.setEmail(email);
            return et;
        }
        return null;
    });
    
    //  PARTIE MODIFIÉE 
    dialog.showAndWait().ifPresent(et -> {
        // Sauvegarder l'étudiant dans la base
        db.saveEtudiant(et);
        
        // Nom d'utilisateur = numéro étudiant
        String nomUtilisateur = et.getNumEtudiant();
        // Mot de passe = nom + prenom + dateNaissance (sans /, en minuscules)
        String mdpUtilisateur = et.getNom().toLowerCase() + et.getPrenom().toLowerCase() + et.getDateNaissance().replace("/", "");
        
        // Créer l'utilisateur dans la table utilisateur
        db.creerUtilisateurEtudiant(nomUtilisateur, mdpUtilisateur, et.getPrenom() + " " + et.getNom());
        
        // Recharger la liste des étudiants
        Promotion promo = comboPromo.getValue();
        if (promo != null) {
            data.setAll(db.getEtudiantsByPromotion(promo.getId()));
        }
        
        // Afficher le message de succès avec les identifiants
        showAlert("Succès", "✅ Étudiant ajouté\n\n🔑 Identifiants de connexion :\nUtilisateur: " + nomUtilisateur + "\nMot de passe: " + mdpUtilisateur, Alert.AlertType.INFORMATION);
    });
    
});
        
        // Archiver un étudiant
        btnArchiver.setOnAction(e -> {
            Etudiant selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Information", "Veuillez sélectionner un étudiant", Alert.AlertType.WARNING);
                return;
            }
            db.archiverEtudiant(selected.getId(), true);
            Promotion p = comboPromo.getValue();
            if (p != null) {
                data.setAll(db.getEtudiantsByPromotion(p.getId()));
            }
            showAlert("Succès", "✅ Étudiant archivé avec succès !", Alert.AlertType.INFORMATION);
        });
        
        
        
        VBox content = new VBox(15, selectorBox, table, boutons);
        content.setPadding(new Insets(20));
        panel.getChildren().add(content);
        
        return panel;
    }

    //  PANNEAU ÉTUDIANTS ARCHIVES 
private Node panneauEtudiantsArchives() {
    VBox panel = createStyledPanel("📦 Étudiants archivés");
    
    // Sélecteur de promotion
    HBox selectorBox = new HBox(10);
    selectorBox.setAlignment(Pos.CENTER_LEFT);
    selectorBox.setPadding(new Insets(0, 0, 15, 0));
    
    Label selectPromoLabel = new Label("Sélectionner une promotion :");
    selectPromoLabel.setTextFill(Color.web(TEXT_PRIMARY));
    
    ComboBox<Promotion> comboPromo = createStyledComboBox(getPromotionsFiltrees());
    
    
    Button btnCharger = createButton("🔄 Charger les étudiants archivés", COLOR_ACCENT);
    
    selectorBox.getChildren().addAll(selectPromoLabel, comboPromo, btnCharger);
    
    // Tableau des étudiants archivés
    TableView<Etudiant> table = new TableView<>();
    
    
    TableColumn<Etudiant, Integer> colId = new TableColumn<>("ID");
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colId.setPrefWidth(50);
    
    TableColumn<Etudiant, String> colPrenom = new TableColumn<>("Prénom");
    colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
    colPrenom.setPrefWidth(150);
    
    TableColumn<Etudiant, String> colNom = new TableColumn<>("Nom");
    colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
    colNom.setPrefWidth(150);
    
    TableColumn<Etudiant, String> colNum = new TableColumn<>("Numéro");
    colNum.setCellValueFactory(new PropertyValueFactory<>("numEtudiant"));
    colNum.setPrefWidth(120);
    
    TableColumn<Etudiant, String> colDateNaiss = new TableColumn<>("Date naiss.");
    colDateNaiss.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
    colDateNaiss.setPrefWidth(100);
    
    TableColumn<Etudiant, String> colTelephone = new TableColumn<>("Téléphone");
    colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    colTelephone.setPrefWidth(120);
    
    TableColumn<Etudiant, String> colEmail = new TableColumn<>("Email");
    colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    colEmail.setPrefWidth(180);
    
    table.getColumns().addAll(colId, colPrenom, colNom, colNum, colDateNaiss, colTelephone, colEmail);
    
    ObservableList<Etudiant> data = FXCollections.observableArrayList();
    table.setItems(data);
    
    // Bouton pour restaurer un étudiant
    HBox boutons = new HBox(10);
    boutons.setAlignment(Pos.CENTER_LEFT);
    boutons.setPadding(new Insets(10, 0, 10, 0));
    
    Button btnRestaurer = createButton("🔄 Restaurer l'étudiant", COLOR_SUCCESS);
    Button btnSupprimerArch = createButton("🗑️ Supprimer", COLOR_DANGER);
    boutons.getChildren().addAll(btnRestaurer, btnSupprimerArch);
    
    btnSupprimerArch.setOnAction(e -> {
        Etudiant selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Information", "Veuillez sélectionner un étudiant", Alert.AlertType.WARNING);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression définitive");
        confirm.setHeaderText("Suppression de l'étudiant");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer DÉFINITIVEMENT " + selected.getPrenom() + " " + selected.getNom() + " ? Cela supprimera aussi son compte utilisateur, ses notes et ses validations.");
        applyThemeToDialog(confirm.getDialogPane());
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                db.deleteEtudiantComplet(selected.getId());
                btnCharger.fire();
                showAlert("Succès", "Étudiant supprimé définitivement !", Alert.AlertType.INFORMATION);
            }
        });
    });
    
    // Charger les étudiants archivés
    btnCharger.setOnAction(e -> {
        Promotion p = comboPromo.getValue();
        if (p == null) {
            showAlert("Information", "Veuillez sélectionner une promotion", Alert.AlertType.WARNING);
            return;
        }
        data.clear();
        for (Etudiant et : db.getAllEtudiants()) {
            if (et.isArchive() && et.getPromotion() != null && et.getPromotion().getId() == p.getId()) {
                data.add(et);
            }
        }
        if (data.isEmpty()) {
            showAlert("Information", "Aucun étudiant archivé dans cette promotion", Alert.AlertType.INFORMATION);
        }
    });
    
    // Restaurer un étudiant
    btnRestaurer.setOnAction(e -> {
        Etudiant selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Information", "Veuillez sélectionner un étudiant", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Restaurer l'étudiant");
        confirm.setContentText("Êtes-vous sûr de vouloir restaurer " + selected.getPrenom() + " " + selected.getNom() + " ?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                db.archiverEtudiant(selected.getId(), false);
                Promotion p = comboPromo.getValue();
                if (p != null) {
                    data.setAll(db.getEtudiantsByPromotion(p.getId()));
                }
                // Rafraîchir aussi le panneau des étudiants actifs
                refreshEtudiantsPanel();
                showAlert("Succès", "✅ Étudiant restauré avec succès !", Alert.AlertType.INFORMATION);
                btnCharger.fire();
            }
        });
    });
    
    VBox content = new VBox(15, selectorBox, table, boutons);
    content.setPadding(new Insets(20));
    panel.getChildren().add(content);
    
    return panel;
}


private void refreshEtudiantsPanel() {

}

// À ajouter dans InterfaceFx.java

private Node panneauEnseignants() {
    VBox panel = createStyledPanel("👨‍🏫 Gestion des enseignants");
    
    TableView<Enseignant> table = new TableView<>();
    
    TableColumn<Enseignant, Integer> colId = new TableColumn<>("ID");
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colId.setPrefWidth(80);
    
    TableColumn<Enseignant, String> colNom = new TableColumn<>("Nom");
    colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
    colNom.setPrefWidth(250);
    
    TableColumn<Enseignant, String> colUtilisateur = new TableColumn<>("Nom d'utilisateur");
    colUtilisateur.setCellValueFactory(cellData -> new SimpleStringProperty(
        cellData.getValue().getNom().replaceAll("\\s+", "")
    ));
    colUtilisateur.setPrefWidth(200);
    
    table.getColumns().addAll(colId, colNom, colUtilisateur);
    
    ObservableList<Enseignant> data = FXCollections.observableArrayList(db.getAllEnseignants());
    table.setItems(data);
    
    HBox boutons = new HBox(10);
    boutons.setAlignment(Pos.CENTER_LEFT);
    boutons.setPadding(new Insets(10, 0, 10, 0));
    
    Button btnAjouter = createButton("➕ Ajouter", COLOR_SUCCESS);
    Button btnModifier = createButton("✏️ Modifier", COLOR_WARNING);
    Button btnSupprimer = createButton("🗑️ Supprimer", COLOR_DANGER);
    Button btnRafraichir = createButton("🔄 Rafraîchir", COLOR_ACCENT);
    
    boutons.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer, btnRafraichir);
    
    btnAjouter.setOnAction(e -> {
        Dialog<Enseignant> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un enseignant");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom complet");
        nomField.setStyle("-fx-background-color: " + BG_INPUT + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-border-color: " + BORDER_COLOR + ";-fx-border-radius: 8;");
        
        Label nomLbl = new Label("Nom complet:");
        nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
        grid.add(nomLbl, 0, 0);
        grid.add(nomField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyThemeToDialog(dialog.getDialogPane());
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String nom = nomField.getText().trim();
                if (nom.isEmpty()) {
                    showAlert("Erreur", "Veuillez saisir un nom", Alert.AlertType.ERROR);
                    return null;
                }
                int newId = (int) (System.currentTimeMillis() % 10000);
                return new Enseignant(newId, nom);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(ens -> {
            db.saveEnseignant(ens);
            
            String username = ens.getNom().replaceAll("\\s+", "");
            String password = username + "123";
            db.creerUtilisateurEnseignant(username, password);
            
            data.setAll(db.getAllEnseignants());
            showAlert("Succès", "✅ Enseignant ajouté\n\n🔑 Identifiants :\nUtilisateur: " + username + "\nMot de passe: " + password, Alert.AlertType.INFORMATION);
        });
    });
    
    btnModifier.setOnAction(e -> {
        Enseignant selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Information", "Veuillez sélectionner un enseignant", Alert.AlertType.WARNING);
            return;
        }
        
        String ancienNom = selected.getNom();
        
        Dialog<Enseignant> dialog = new Dialog<>();
        dialog.setTitle("Modifier un enseignant");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nomField = new TextField(selected.getNom());
        nomField.setStyle("-fx-background-color: " + BG_INPUT + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-border-color: " + BORDER_COLOR + ";-fx-border-radius: 8;");
        
        Label nomLbl = new Label("Nom complet:");
        nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
        grid.add(nomLbl, 0, 0);
        grid.add(nomField, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyThemeToDialog(dialog.getDialogPane());
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String nom = nomField.getText().trim();
                if (nom.isEmpty()) {
                    showAlert("Erreur", "Veuillez saisir un nom", Alert.AlertType.ERROR);
                    return null;
                }
                selected.setNom(nom);
                return selected;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(ens -> {
            db.saveEnseignant(ens);
            
            if (!ancienNom.equals(ens.getNom())) {
                String ancienUsername = ancienNom.replaceAll("\\s+", "");
                String nouveauUsername = ens.getNom().replaceAll("\\s+", "");
                db.supprimerUtilisateurEnseignant(ancienUsername);
                String password = nouveauUsername + "123";
                db.creerUtilisateurEnseignant(nouveauUsername, password);
                showAlert("Succès", "✅ Enseignant modifié\n\n🔑 Nouveaux identifiants :\nUtilisateur: " + nouveauUsername + "\nMot de passe: " + password, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Succès", "✅ Enseignant modifié !", Alert.AlertType.INFORMATION);
            }
            data.setAll(db.getAllEnseignants());
        });
    });
    
    btnSupprimer.setOnAction(e -> {
        Enseignant selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Information", "Veuillez sélectionner un enseignant", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Supprimer " + selected.getNom() + " ?\n\n⚠️ Le compte utilisateur sera aussi supprimé.");
        applyThemeToDialog(confirm.getDialogPane());
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                db.deleteEnseignant(selected.getId());
                data.setAll(db.getAllEnseignants());
                showAlert("Succès", "✅ Enseignant supprimé !", Alert.AlertType.INFORMATION);
            }
        });
    });
    
    btnRafraichir.setOnAction(e -> data.setAll(db.getAllEnseignants()));
    
    VBox content = new VBox(15, boutons, table);
    content.setPadding(new Insets(20));
    panel.getChildren().add(content);
    
    return panel;
}
    
    //  PANNEAU PROMOTIONS 
    private Node panneauPromotions() {
        VBox panel = createStyledPanel("🏫 Gestion des promotions");
        
        TableView<Promotion> table = new TableView<>();
        
        
        TableColumn<Promotion, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Promotion, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(200);
        
        TableColumn<Promotion, String> colFiliere = new TableColumn<>("Filière");
        colFiliere.setCellValueFactory(new PropertyValueFactory<>("filiere"));
        colFiliere.setPrefWidth(150);
        
        TableColumn<Promotion, String> colAnnee = new TableColumn<>("Année");
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneeAcademique"));
        colAnnee.setPrefWidth(120);
        
        table.getColumns().addAll(colId, colNom, colFiliere, colAnnee);
        
        ObservableList<Promotion> data = FXCollections.observableArrayList(db.getAllPromotions());
        if (roleActuel.equals("RESPONSABLE_FILIERE") && filiereResponsable != null) {
            data.removeIf(p -> !p.getFiliere().equals(filiereResponsable));
        }
        table.setItems(data);
        
        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_LEFT);
        boutons.setPadding(new Insets(10, 0, 10, 0));
        
        Button btnAjouter = createButton("➕ Ajouter", COLOR_SUCCESS);
        Button btnModifier = createButton("✏️ Modifier", COLOR_WARNING);
        Button btnSupprimer = createButton("🗑️ Supprimer", COLOR_DANGER);
        Button btnRafraichir = createButton("🔄 Rafraîchir", COLOR_ACCENT);
        
        boutons.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer, btnRafraichir);
        
        btnAjouter.setOnAction(e -> {
            Dialog<Promotion> dialog = new Dialog<>();
            dialog.setTitle("Ajouter une promotion");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(primaryStage);
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            
            TextField nomField = new TextField();
            TextField filiereField = new TextField();
            TextField anneeField = new TextField();
            
            Label nomLbl = new Label("Nom:");
            nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(nomLbl, 0, 0);
            grid.add(nomField, 1, 0);
            
            Label filiereLbl = new Label("Filière:");
            filiereLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(filiereLbl, 0, 1);
            grid.add(filiereField, 1, 1);
            
            Label anneeLbl = new Label("Année académique:");
            anneeLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(anneeLbl, 0, 2);
            grid.add(anneeField, 1, 2);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            applyThemeToDialog(dialog.getDialogPane());
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    int newId = (int) (System.currentTimeMillis() % 10000);
                    return new Promotion(newId, nomField.getText(), filiereField.getText(), anneeField.getText());
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(p -> {
                db.savePromotion(p);
                data.setAll(db.getAllPromotions());
                showAlert("Succès", "Promotion ajoutée avec succès !", Alert.AlertType.INFORMATION);
            });
        });
        
        btnModifier.setOnAction(e -> {
            Promotion selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Information", "Veuillez sélectionner une promotion", Alert.AlertType.WARNING);
                return;
            }
            
            Dialog<Promotion> dialog = new Dialog<>();
            dialog.setTitle("Modifier une promotion");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            
            TextField nomField = new TextField(selected.getNom());
            TextField filiereField = new TextField(selected.getFiliere());
            TextField anneeField = new TextField(selected.getAnneeAcademique());
            
            Label nomLbl = new Label("Nom:");
            nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(nomLbl, 0, 0);
            grid.add(nomField, 1, 0);
            
            Label filiereLbl = new Label("Filière:");
            filiereLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(filiereLbl, 0, 1);
            grid.add(filiereField, 1, 1);
            
            Label anneeLbl = new Label("Année académique:");
            anneeLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(anneeLbl, 0, 2);
            grid.add(anneeField, 1, 2);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            applyThemeToDialog(dialog.getDialogPane());
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    selected.setNom(nomField.getText());
                    selected.setFiliere(filiereField.getText());
                    selected.setAnneeAcademique(anneeField.getText());
                    return selected;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(p -> {
                db.savePromotion(p);
                data.setAll(db.getAllPromotions());
                showAlert("Succès", "Promotion modifiée avec succès !", Alert.AlertType.INFORMATION);
            });
        });
        
        btnSupprimer.setOnAction(e -> {
            Promotion selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Supprimer cette promotion ?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    db.deletePromotion(selected.getId());
                    data.setAll(db.getAllPromotions());
                    showAlert("Succès", "Promotion supprimée avec succès !", Alert.AlertType.INFORMATION);
                }
            });
        });
        
        btnRafraichir.setOnAction(e -> data.setAll(db.getAllPromotions()));
        
        VBox content = new VBox(15, boutons, table);
        content.setPadding(new Insets(20));
        panel.getChildren().add(content);
        
        return panel;
    }
    
    //  PANNEAU MODULES 
    private Node panneauModules() {
        VBox panel = createStyledPanel("📚 Gestion des modules");
        
        Accordion accordion = new Accordion();
        
        VBox modulesBox = new VBox(10);
        modulesBox.setPadding(new Insets(10));
        
        TableView<Module> tableModules = new TableView<>();
        
        
        TableColumn<Module, Integer> colModId = new TableColumn<>("ID");
        colModId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Module, String> colModNom = new TableColumn<>("Nom");
        colModNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableColumn<Module, String> colModDesc = new TableColumn<>("Description");
        colModDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colModDesc.setPrefWidth(300);
        
        tableModules.getColumns().addAll(colModId, colModNom, colModDesc);
        ObservableList<Module> modulesData = FXCollections.observableArrayList(db.getAllModules());
        tableModules.setItems(modulesData);
        
        HBox modulesBoutons = new HBox(10);
        Button btnAjouterModule = createButton("➕ Ajouter Module", COLOR_SUCCESS);
        Button btnModifierModule = createButton("✏️ Modifier Module", COLOR_WARNING);
        Button btnSupprimerModule = createButton("🗑️ Supprimer Module", COLOR_DANGER);
        Button btnRafraichirModules = createButton("🔄 Rafraîchir", COLOR_ACCENT);
        modulesBoutons.getChildren().addAll(btnAjouterModule, btnModifierModule, btnSupprimerModule, btnRafraichirModules);
        
        modulesBox.getChildren().addAll(modulesBoutons, tableModules);
        
        VBox sousModulesBox = new VBox(10);
        sousModulesBox.setPadding(new Insets(10));
        
        TableView<SousModule> tableSousModules = new TableView<>();
        
        
        TableColumn<SousModule, Integer> colSmId = new TableColumn<>("ID");
        colSmId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSmId.setPrefWidth(50);
        TableColumn<SousModule, String> colSmNom = new TableColumn<>("Nom");
        colSmNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colSmNom.setPrefWidth(180);
        TableColumn<SousModule, Double> colCoef = new TableColumn<>("Coefficient");
        colCoef.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        colCoef.setPrefWidth(80);
        TableColumn<SousModule, String> colModule = new TableColumn<>("Module");
        colModule.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModuleParent().getNom()));
        colModule.setPrefWidth(180);
        TableColumn<SousModule, String> colPromo = new TableColumn<>("Promotion");
        colPromo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPromotion().getNom()));
        colPromo.setPrefWidth(150);
        TableColumn<SousModule, String> colEnseignant = new TableColumn<>("Enseignant");
        colEnseignant.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getEnseignantAssigne() != null ? cellData.getValue().getEnseignantAssigne().getNom() : "Non assigné"));
        colEnseignant.setPrefWidth(180);
        
        tableSousModules.getColumns().addAll(colSmId, colSmNom, colCoef, colModule, colPromo, colEnseignant);
        ObservableList<SousModule> sousModulesData = FXCollections.observableArrayList(db.getAllSousModules());
        tableSousModules.setItems(sousModulesData);
        
        HBox sousModulesBoutons = new HBox(10);
        Button btnAjouterSM = createButton("➕ Ajouter Sous-module", COLOR_SUCCESS);
        Button btnModifierSM = createButton("✏️ Modifier Sous-module", COLOR_WARNING);
        Button btnSupprimerSM = createButton("🗑️ Supprimer Sous-module", COLOR_DANGER);
        Button btnRafraichirSM = createButton("🔄 Rafraîchir", COLOR_ACCENT);
        sousModulesBoutons.getChildren().addAll(btnAjouterSM, btnModifierSM, btnSupprimerSM, btnRafraichirSM);
        
        sousModulesBox.getChildren().addAll(sousModulesBoutons, tableSousModules);
        
        TitledPane tpModules = new TitledPane("📦 Modules", modulesBox);
        TitledPane tpSousModules = new TitledPane("📄 Sous-modules", sousModulesBox);
        accordion.getPanes().addAll(tpModules, tpSousModules);
        
        //  ACTIONS MODULES 
        btnAjouterModule.setOnAction(e -> {
            Dialog<Module> dialog = new Dialog<>();
            dialog.setTitle("Ajouter un module");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            
            TextField nomField = new TextField();
            TextField descField = new TextField();
            
            Label nomLbl = new Label("Nom:");
            nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(nomLbl, 0, 0);
            grid.add(nomField, 1, 0);
            
            Label descLbl = new Label("Description:");
            descLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(descLbl, 0, 1);
            grid.add(descField, 1, 1);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            applyThemeToDialog(dialog.getDialogPane());
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    int newId = (int) (System.currentTimeMillis() % 10000);
                    return new Module(newId, nomField.getText(), descField.getText());
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(m -> {
                db.saveModule(m);
                modulesData.setAll(db.getAllModules());
                showAlert("Succès", "✅ Module ajouté avec succès !", Alert.AlertType.INFORMATION);
            });
        });
        
        btnModifierModule.setOnAction(e -> {
            Module selected = tableModules.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            
            Dialog<Module> dialog = new Dialog<>();
            dialog.setTitle("Modifier un module");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            
            TextField nomField = new TextField(selected.getNom());
            TextField descField = new TextField(selected.getDescription());
            
            Label nomLbl = new Label("Nom:");
            nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(nomLbl, 0, 0);
            grid.add(nomField, 1, 0);
            
            Label descLbl = new Label("Description:");
            descLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(descLbl, 0, 1);
            grid.add(descField, 1, 1);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            applyThemeToDialog(dialog.getDialogPane());
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    selected.setNom(nomField.getText());
                    selected.setDescription(descField.getText());
                    return selected;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(m -> {
                db.saveModule(m);
                modulesData.setAll(db.getAllModules());
                sousModulesData.setAll(db.getAllSousModules());
                showAlert("Succès", "✅ Module modifié avec succès !", Alert.AlertType.INFORMATION);
            });
        });
        
        btnSupprimerModule.setOnAction(e -> {
            Module selected = tableModules.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setContentText("Supprimer ce module ?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    db.deleteModule(selected.getId());
                    modulesData.setAll(db.getAllModules());
                    sousModulesData.setAll(db.getAllSousModules());
                    showAlert("Succès", "✅ Module supprimé avec succès !", Alert.AlertType.INFORMATION);
                }
            });
        });
        
        btnRafraichirModules.setOnAction(e -> {
            modulesData.setAll(db.getAllModules());
            sousModulesData.setAll(db.getAllSousModules());
        });
        
        //  ACTIONS SOUS-MODULES 
        btnAjouterSM.setOnAction(e -> {
            if (db.getAllModules().isEmpty() || db.getAllPromotions().isEmpty()) {
                showAlert("Information", "Veuillez d'abord créer des modules et des promotions", Alert.AlertType.WARNING);
                return;
            }
            
            Dialog<SousModule> dialog = new Dialog<>();
            dialog.setTitle("Ajouter un sous-module");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            
            TextField nomField = new TextField();
            nomField.setPromptText("Nom du sous-module");
            nomField.setStyle("-fx-background-color: " + BG_INPUT + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-border-color: " + BORDER_COLOR + ";-fx-border-radius: 8;");
            
            TextField coefField = new TextField();
            coefField.setPromptText("Coefficient");
            coefField.setStyle("-fx-background-color: " + BG_INPUT + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-border-color: " + BORDER_COLOR + ";-fx-border-radius: 8;");
            
            ComboBox<Module> moduleCombo = createStyledComboBox(FXCollections.observableArrayList(db.getAllModules()));
            
            
            ComboBox<Promotion> promoCombo = createStyledComboBox(FXCollections.observableArrayList(db.getAllPromotions()));
            
            
            ComboBox<Enseignant> ensCombo = createStyledComboBox(FXCollections.observableArrayList(db.getAllEnseignants()));
            
            
            
            Label nomLbl = new Label("Nom:");
            nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(nomLbl, 0, 0);
            grid.add(nomField, 1, 0);
            
            Label coefLbl = new Label("Coefficient:");
            coefLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(coefLbl, 0, 1);
            grid.add(coefField, 1, 1);
            
            Label moduleDialLbl = new Label("Module:");
            moduleDialLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(moduleDialLbl, 0, 2);
            grid.add(moduleCombo, 1, 2);
            
            Label promoDialLbl = new Label("Promotion:");
            promoDialLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(promoDialLbl, 0, 3);
            grid.add(promoCombo, 1, 3);
            
            Label enseignantDialLbl = new Label("Enseignant assigné:");
            enseignantDialLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(enseignantDialLbl, 0, 4);
            grid.add(ensCombo, 1, 4);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            applyThemeToDialog(dialog.getDialogPane());
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    int newId = (int) (System.currentTimeMillis() % 10000);
                    SousModule sm = new SousModule(newId, nomField.getText(), Double.parseDouble(coefField.getText()),
                            moduleCombo.getValue(), promoCombo.getValue());
                    sm.setEnseignantAssigne(ensCombo.getValue());
                    return sm;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(sm -> {
                db.saveSousModule(sm);
                sousModulesData.setAll(db.getAllSousModules());
                showAlert("Succès", "✅ Sous-module ajouté avec succès !", Alert.AlertType.INFORMATION);
            });
        });
        
        btnModifierSM.setOnAction(e -> {
            SousModule selected = tableSousModules.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            
            Dialog<SousModule> dialog = new Dialog<>();
            dialog.setTitle("Modifier un sous-module");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            
            TextField nomField = new TextField(selected.getNom());
            nomField.setStyle("-fx-background-color: " + BG_INPUT + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-border-color: " + BORDER_COLOR + ";-fx-border-radius: 8;");
            
            TextField coefField = new TextField(String.valueOf(selected.getCoefficient()));
            coefField.setStyle("-fx-background-color: " + BG_INPUT + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-border-color: " + BORDER_COLOR + ";-fx-border-radius: 8;");
            
            ComboBox<Module> moduleCombo = createStyledComboBox(FXCollections.observableArrayList(db.getAllModules()));
            moduleCombo.setValue(selected.getModuleParent());
            
            ComboBox<Promotion> promoCombo = createStyledComboBox(FXCollections.observableArrayList(db.getAllPromotions()));
            promoCombo.setValue(selected.getPromotion());
            
            ComboBox<Enseignant> ensCombo = createStyledComboBox(FXCollections.observableArrayList(db.getAllEnseignants()));
            ensCombo.setValue(selected.getEnseignantAssigne());
            
            Label nomLbl = new Label("Nom:");
            nomLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(nomLbl, 0, 0);
            grid.add(nomField, 1, 0);
            
            Label coefLbl = new Label("Coefficient:");
            coefLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(coefLbl, 0, 1);
            grid.add(coefField, 1, 1);
            
            Label moduleDialLbl = new Label("Module:");
            moduleDialLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(moduleDialLbl, 0, 2);
            grid.add(moduleCombo, 1, 2);
            
            Label promoDialLbl = new Label("Promotion:");
            promoDialLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(promoDialLbl, 0, 3);
            grid.add(promoCombo, 1, 3);
            
            Label enseignantDialLbl = new Label("Enseignant assigné:");
            enseignantDialLbl.setTextFill(Color.web(TEXT_PRIMARY));
            grid.add(enseignantDialLbl, 0, 4);
            grid.add(ensCombo, 1, 4);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            applyThemeToDialog(dialog.getDialogPane());
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    selected.setNom(nomField.getText());
                    selected.setCoefficient(Double.parseDouble(coefField.getText()));
                    selected.setModuleParent(moduleCombo.getValue());
                    selected.setPromotion(promoCombo.getValue());
                    selected.setEnseignantAssigne(ensCombo.getValue());
                    return selected;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(sm -> {
                db.saveSousModule(sm);
                sousModulesData.setAll(db.getAllSousModules());
                showAlert("Succès", "✅ Sous-module modifié avec succès !", Alert.AlertType.INFORMATION);
            });
        });
        
        btnSupprimerSM.setOnAction(e -> {
            SousModule selected = tableSousModules.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setContentText("Supprimer ce sous-module ?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    db.deleteSousModule(selected.getId());
                    sousModulesData.setAll(db.getAllSousModules());
                    showAlert("Succès", "✅ Sous-module supprimé avec succès !", Alert.AlertType.INFORMATION);
                }
            });
        });
        
        btnRafraichirSM.setOnAction(e -> sousModulesData.setAll(db.getAllSousModules()));
        
        panel.getChildren().add(accordion);
        panel.setPadding(new Insets(20));
        
        return panel;
    }
    
    //  PANNEAU SAISIE NOTES 
    private Node panneauSaisieNotes() {
        VBox panel = createStyledPanel("📝 Saisie des notes");
        
        Enseignant enseignant = db.getEnseignantParNom(nomUtilisateur);
        
        if (enseignant == null || db.getSousModulesByEnseignant(enseignant.getId()).isEmpty()) {
            Label message = new Label("📭 Aucun sous-module assigné");
            message.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            message.setTextFill(Color.web(COLOR_DANGER));
            panel.getChildren().add(message);
            return panel;
        }
        
        List<SousModule> mesModules = db.getSousModulesByEnseignant(enseignant.getId());
        ComboBox<SousModule> comboModule = createStyledComboBox(FXCollections.observableArrayList(mesModules));
        
        TableView<NoteTableRow> table = new TableView<>();
        
        
        TableColumn<NoteTableRow, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);
        
        TableColumn<NoteTableRow, String> colEtudiant = new TableColumn<>("Étudiant");
        colEtudiant.setCellValueFactory(new PropertyValueFactory<>("etudiantNom"));
        colEtudiant.setPrefWidth(300);
        
        TableColumn<NoteTableRow, Double> colNote = new TableColumn<>("Note /20");
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colNote.setPrefWidth(120);
        colNote.setEditable(true);
        
        TableColumn<NoteTableRow, Double> colNoteRattrapage = new TableColumn<>("Rattrapage /20");
        colNoteRattrapage.setCellValueFactory(new PropertyValueFactory<>("noteRattrapage"));
        colNoteRattrapage.setPrefWidth(120);
        colNoteRattrapage.setEditable(true);
        
        // Factory that works for both Note and NoteRattrapage, handles negative values as empty
        javafx.util.Callback<TableColumn<NoteTableRow, Double>, TableCell<NoteTableRow, Double>> cellFactory = tc -> new TableCell<NoteTableRow, Double>() {
            private TextField textField;
            
            @Override
            public void startEdit() {
                if (!isEmpty() && getTableRow() != null && getTableRow().getItem() != null) {
                    super.startEdit();
                    createTextField();
                    setText(null);
                    setGraphic(textField);
                    textField.selectAll();
                    textField.requestFocus();
                }
            }
            
            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem() != null && getItem() >= 0 ? String.format("%.2f", getItem()) : "-");
                setGraphic(null);
            }
            
            @Override
            public void commitEdit(Double newValue) {
                super.commitEdit(newValue);
                NoteTableRow row = getTableRow().getItem();
                if (row != null) {
                    if (tc.getText().startsWith("Note")) {
                        row.setNote(newValue);
                        // Si la note devient >= 11, on annule l'éventuel rattrapage
                        if (newValue >= 11 && row.getNoteRattrapage() != -1) {
                            row.setNoteRattrapage(-1);
                        }
                    } else {
                        row.setNoteRattrapage(newValue);
                    }
                    if (!hasUnsavedChanges) {
                        hasUnsavedChanges = true;
                        updateLampeIndicateur();
                    }
                    
                    // Forcer le rafraîchissement visuel de toute la ligne (pour mettre à jour l'autre colonne instantanément)
                    javafx.application.Platform.runLater(() -> getTableView().refresh());
                }
            }
            
            private void createTextField() {
                textField = new TextField(getItem() != null && getItem() >= 0 ? String.valueOf(getItem()) : "");
                textField.setStyle("-fx-background-color: " + BG_SECONDARY + ";-fx-text-fill: " + TEXT_PRIMARY + ";-fx-background-radius: 8;-fx-border-color: " + COLOR_ACCENT + ";-fx-border-radius: 8;");
                textField.setOnAction(e -> processInput());
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        processInput();
                    }
                });
            }
            
            private boolean isProcessing = false;
            
            private void processInput() {
                if (isProcessing) return;
                isProcessing = true;
                try {
                    String t = textField.getText().trim();
                    double value = (t.isEmpty() || t.equals("-")) ? -1.0 : Double.parseDouble(t);
                    
                    if (value != -1 && (value < 0 || value > 20)) {
                        showAlert("Erreur", "La note doit être comprise entre 0 et 20", Alert.AlertType.ERROR);
                        cancelEdit();
                        return;
                    }
                    
                    NoteTableRow row = getTableRow().getItem();
                    if (row != null && !tc.getText().startsWith("Note")) {
                        if (row.getNote() >= 11 && value != -1) {
                            showAlert("Erreur", "L'étudiant a déjà validé ce module (note >= 11). Un rattrapage n'est pas autorisé.", Alert.AlertType.ERROR);
                            cancelEdit();
                            return;
                        }
                    }
                    
                    commitEdit(value);
                } catch (NumberFormatException ex) {
                    cancelEdit();
                } finally {
                    isProcessing = false;
                }
            }
            
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        if (textField != null) {
                            textField.setText(item >= 0 ? String.valueOf(item) : "");
                        }
                        setText(null);
                        setGraphic(textField);
                    } else {
                        setText(item >= 0 ? String.format("%.2f", item) : "-");
                        setGraphic(null);
                    }
                }
            }
        };
        
        colNote.setCellFactory(cellFactory);
        colNoteRattrapage.setCellFactory(cellFactory);
        
        table.getColumns().addAll(colId, colEtudiant, colNote, colNoteRattrapage);
        table.setEditable(true);
        
        ObservableList<NoteTableRow> data = FXCollections.observableArrayList();
        table.setItems(data);
        
        // Panneau supérieur avec sélecteur et bouton charger
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label selectLabel = new Label("📌 Sous-module:");
        selectLabel.setTextFill(Color.web(TEXT_PRIMARY));
        Button btnCharger = createButton("🔄 Charger", COLOR_ACCENT);
        topBox.getChildren().addAll(selectLabel, comboModule, btnCharger);
        topBox.setPadding(new Insets(0, 0, 15, 0));
        
        // Panneau des boutons avec indicateur
        HBox bottomBox = new HBox(15);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(15, 0, 0, 0));
        
        // Lampe indicateur
        lampeIndicateur = new Label("●");
        lampeIndicateur.setFont(Font.font("Segoe UI", 20));
        lampeIndicateur.setTextFill(Color.web(COLOR_DANGER));
        Label statusLabel = new Label("Enregistrement ");
        statusLabel.setTextFill(Color.web(TEXT_SECONDARY));
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        
        VBox lampeBox = new VBox(2);
        lampeBox.setAlignment(Pos.CENTER);
        lampeBox.getChildren().addAll(lampeIndicateur, statusLabel);
        
        Button btnEnregistrer = createButton("💾 Enregistrer notes", COLOR_SUCCESS);
        Button btnImporterExcel = createButton("📥 Importer Excel", COLOR_WARNING);
        Button btnExporterExcel = createButton("📤 Exporter Excel", COLOR_ACCENT);
        
        bottomBox.getChildren().addAll(lampeBox, btnEnregistrer, btnImporterExcel, btnExporterExcel);
        
        // Initialiser l'indicateur
        updateLampeIndicateur();
        
        // Charger les étudiants
        btnCharger.setOnAction(e -> {
            SousModule sm = comboModule.getValue();
            if (sm == null) {
                showAlert("Information", "Veuillez sélectionner un sous-module", Alert.AlertType.WARNING);
                return;
            }
            data.clear();
            for (Etudiant et : db.getEtudiantsByPromotion(sm.getPromotion().getId())) {
                if (!et.isArchive()) {  
                double noteExistante = 0;
                double rattrapageExistant = -1;
                for (Note n : db.getNotesByEtudiant(et.getId())) {
                    if (n.getSousModule().getId() == sm.getId()) {
                        noteExistante = n.getValeur();
                        rattrapageExistant = n.getValeurRattrapage();
                        break;
                    }
                }
                data.add(new NoteTableRow(et.getId(), et.getPrenom() + " " + et.getNom(), noteExistante, rattrapageExistant));
                }
            }
            hasUnsavedChanges = false;
            updateLampeIndicateur();
        });
        
        // Enregistrer les notes
        btnEnregistrer.setOnAction(e -> {
            SousModule sm = comboModule.getValue();
            if (sm == null) return;
            for (NoteTableRow row : data) {
                Etudiant et = db.getEtudiantById(row.getId());
                Note n = new Note(et, sm, row.getNote(), row.getNoteRattrapage());
                db.saveNote(n);
            }
            hasUnsavedChanges = false;
            updateLampeIndicateur();
            showAlert("Succès", "✅ Notes enregistrées avec succès !", Alert.AlertType.INFORMATION);
        });
        
        btnImporterExcel.setOnAction(e -> {
            SousModule sm = comboModule.getValue();
            if (sm == null) {
                showAlert("Information", "Veuillez d'abord sélectionner un sous-module", Alert.AlertType.WARNING);
                return;
            }
            importerNotesExcel(data, sm);
            hasUnsavedChanges = true;
            updateLampeIndicateur();
        });
        
        btnExporterExcel.setOnAction(e -> {
            SousModule sm = comboModule.getValue();
            if (sm == null) {
                showAlert("Information", "Veuillez d'abord sélectionner un sous-module", Alert.AlertType.WARNING);
                return;
            }
            exporterNotesExcel(data, sm);
        });
        
        VBox content = new VBox(15, topBox, table, bottomBox);
        content.setPadding(new Insets(20));
        panel.getChildren().add(content);
        
        return panel;
    }
    
    //  PANNEAU NOTES ÉTUDIANT 
    private Node panneauNotesEtudiant() {
        VBox panel = createStyledPanel("📖 Mes notes");
        
        GridPane infoPanel = new GridPane();
        infoPanel.setHgap(15);
        infoPanel.setVgap(10);
        infoPanel.setPadding(new Insets(15));
        infoPanel.setStyle("-fx-background-color: " + BG_CARD + ";-fx-background-radius: 10;");
        
        Label nomLabel = new Label("👤 Nom complet:");
        nomLabel.setTextFill(Color.web(TEXT_PRIMARY));
        Label nomValue = new Label(etudiantConnecte.getPrenom() + " " + etudiantConnecte.getNom());
        nomValue.setTextFill(Color.web(COLOR_ACCENT));
        infoPanel.add(nomLabel, 0, 0);
        infoPanel.add(nomValue, 1, 0);
        
        Label numLabel = new Label("🆔 Numéro étudiant:");
        numLabel.setTextFill(Color.web(TEXT_PRIMARY));
        Label numValue = new Label(etudiantConnecte.getNumEtudiant());
        numValue.setTextFill(Color.web(COLOR_ACCENT));
        infoPanel.add(numLabel, 0, 1);
        infoPanel.add(numValue, 1, 1);
        
        Label promoLabel = new Label("🏫 Promotion:");
        promoLabel.setTextFill(Color.web(TEXT_PRIMARY));
        Label promoValue = new Label(etudiantConnecte.getPromotion() != null ? etudiantConnecte.getPromotion().getNom() : "Non assigné");
        promoValue.setTextFill(Color.web(COLOR_ACCENT));
        infoPanel.add(promoLabel, 0, 2);
        infoPanel.add(promoValue, 1, 2);
        
        TableView<Object[]> table = new TableView<>();
        
        
        TableColumn<Object[], String> colModule = new TableColumn<>("Module");
        colModule.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue()[0]));
        colModule.setPrefWidth(150);
        
        TableColumn<Object[], String> colSousModule = new TableColumn<>("Sous-module");
        colSousModule.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue()[1]));
        colSousModule.setPrefWidth(150);
        
        TableColumn<Object[], String> colCoef = new TableColumn<>("Coefficient");
        colCoef.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue()[2])));
        colCoef.setPrefWidth(80);
        
        TableColumn<Object[], String> colNote = new TableColumn<>("Note initiale");
        colNote.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue()[3])));
        colNote.setPrefWidth(90);
        
        TableColumn<Object[], String> colRattrapage = new TableColumn<>("Rattrapage");
        colRattrapage.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue()[4])));
        colRattrapage.setPrefWidth(90);
        
        TableColumn<Object[], String> colDefinitive = new TableColumn<>("Définitive");
        colDefinitive.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue()[5])));
        colDefinitive.setPrefWidth(90);
        
        TableColumn<Object[], String> colMoyModule = new TableColumn<>("Moyenne Module");
        colMoyModule.setCellValueFactory(cellData -> new SimpleStringProperty((String) cellData.getValue()[6]));
        colMoyModule.setPrefWidth(120);
        
        table.getColumns().addAll(colModule, colSousModule, colCoef, colNote, colRattrapage, colDefinitive, colMoyModule);
        
        ObservableList<Object[]> data = FXCollections.observableArrayList();
        
        List<Note> notes = db.getNotesByEtudiant(etudiantConnecte.getId());
        List<Module> modules = db.getAllModules();
        
        for (Module m : modules) {
            double sommeNotes = 0;
            double sommeCoef = 0;
            boolean hasNotes = false;
            
            for (Note n : notes) {
                if (n.getSousModule().getModuleParent().getId() == m.getId()) {
                    sommeNotes += n.getNoteDefinitive() * n.getSousModule().getCoefficient();
                    sommeCoef += n.getSousModule().getCoefficient();
                    hasNotes = true;
                }
            }
            double moyenneModule = hasNotes ? sommeNotes / sommeCoef : 0;
            
            for (Note n : notes) {
                if (n.getSousModule().getModuleParent().getId() == m.getId()) {
                    String rattrapageStr = n.getValeurRattrapage() >= 0 ? String.valueOf(n.getValeurRattrapage()) : "-";
                    String valeurStr = n.getValeur() >= 0 ? String.valueOf(n.getValeur()) : "-";
                    String defStr = n.getNoteDefinitive() >= 0 ? String.valueOf(n.getNoteDefinitive()) : "-";
                    String moyModStr = moyenneModule >= 0 ? String.format("%.2f", moyenneModule) : "-";
                    data.add(new Object[]{
                            m.getNom(),
                            n.getSousModule().getNom(),
                            n.getSousModule().getCoefficient(),
                            valeurStr,
                            rattrapageStr,
                            defStr,
                            moyModStr
                    });
                }
            }
        }
        
        table.setItems(data);
        
        double moyenneGenerale = db.calculerMoyenneGenerale(etudiantConnecte.getId());
        
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15, 0, 0, 0));
        
        String moyText = moyenneGenerale >= 0 ? String.format("%.2f", moyenneGenerale) + " / 20" : "En attente de notes";
        Label moyenneLabel = new Label("🎯 MOYENNE GÉNÉRALE : " + moyText);
        moyenneLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        moyenneLabel.setTextFill(Color.web(moyenneGenerale >= 11 ? COLOR_SUCCESS : (moyenneGenerale >= 0 ? COLOR_DANGER : TEXT_SECONDARY)));
        bottomBox.getChildren().add(moyenneLabel);
        
        VBox content = new VBox(15, infoPanel, table, bottomBox);
        content.setPadding(new Insets(20));
        panel.getChildren().add(content);
        
        return panel;
    }
    
    //  PANNEAU VALIDATION ANNÉE 
    private Node panneauValidationAnnee() {
        VBox panel = createStyledPanel("✅ Validation de l'année - Règlement d'évaluation");
        
        ComboBox<Promotion> comboPromo = createStyledComboBox(getPromotionsFiltrees());
        
        TableView<ValidationRow> table = new TableView<>();
        
        
        TableColumn<ValidationRow, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<ValidationRow, String> colNom = new TableColumn<>("Étudiant");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(200);
        
        TableColumn<ValidationRow, String> colMoyGen = new TableColumn<>("Moyenne Générale");
        colMoyGen.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getMoyenneGenerale() >= 0 ? 
                String.format("%.2f", cellData.getValue().getMoyenneGenerale()) : "En attente"
        ));
        colMoyGen.setPrefWidth(120);
        
        TableColumn<ValidationRow, Integer> colModulesNonValides = new TableColumn<>("Modules Non Validés");
        colModulesNonValides.setCellValueFactory(new PropertyValueFactory<>("modulesNonValides"));
        colModulesNonValides.setPrefWidth(150);
        
        TableColumn<ValidationRow, String> colNoteInf6 = new TableColumn<>("Note < 6");
        colNoteInf6.setCellValueFactory(new PropertyValueFactory<>("noteInferieure6"));
        colNoteInf6.setPrefWidth(100);
        
        TableColumn<ValidationRow, String> colValide = new TableColumn<>("Validation");
        colValide.setCellValueFactory(new PropertyValueFactory<>("valide"));
        colValide.setPrefWidth(100);
        
        table.getColumns().addAll(colId, colNom, colMoyGen, colModulesNonValides, colNoteInf6, colValide);
        
        ObservableList<ValidationRow> data = FXCollections.observableArrayList();
        table.setItems(data);
        
        Button btnCalculer = createButton("📊 Calculer validation", COLOR_ACCENT);
        Button btnValider = createButton("✅ Valider sélectionné", COLOR_SUCCESS);
        Button btnInvalider = createButton("❌ Invalider sélectionné", COLOR_DANGER);
        
     btnCalculer.setOnAction(e -> {
    Promotion p = comboPromo.getValue();
    if (p == null) return;
    data.clear();
    List<Module> modules = db.getAllModules();
    for (Etudiant et : db.getEtudiantsByPromotion(p.getId())) {
        if (!et.isArchive()) {  // AJOUTEZ CETTE CONDITION
            ReglementEvaluation.ResultatValidation result = ReglementEvaluation.validerAnnee(et, modules, db);
            
            boolean dejaValide = db.isValidationExistante(et.getId(), p.getId());
            
            data.add(new ValidationRow(et.getId(), et.getNomComplet(), 
                    result.moyenneGenerale, result.modulesNonValides,
                    result.noteModuleInferieure6 ? "❌ Oui" : "✅ Non",
                    dejaValide ? "✅ VALIDÉ" : "❌ NON VALIDÉ"));
        }
    }
});
        
        btnValider.setOnAction(e -> {
            ValidationRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Information", "Veuillez sélectionner un étudiant", Alert.AlertType.WARNING);
                return;
            }
            Promotion p = comboPromo.getValue();
            if (p != null) {
                db.marquerValidation(selected.getId(), p.getId(), true);
                showAlert("Succès", "Étudiant validé manuellement (Décision Jury) !", Alert.AlertType.INFORMATION);
                btnCalculer.fire();
            }
        });
        
        btnInvalider.setOnAction(e -> {
            ValidationRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Information", "Veuillez sélectionner un étudiant", Alert.AlertType.WARNING);
                return;
            }
            Promotion p = comboPromo.getValue();
            if (p != null) {
                db.marquerValidation(selected.getId(), p.getId(), false);
                showAlert("Succès", "Validation refusée manuellement (Décision Jury) !", Alert.AlertType.INFORMATION);
                btnCalculer.fire();
            }
        });
        
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label promoTitleLabel = new Label("Promotion:");
        promoTitleLabel.setTextFill(Color.web(TEXT_PRIMARY));
        topBox.getChildren().addAll(promoTitleLabel, comboPromo, btnCalculer, btnValider, btnInvalider);
        topBox.setPadding(new Insets(10));
        
        VBox reglesBox = new VBox(5);
        reglesBox.setPadding(new Insets(10));
        reglesBox.setStyle("-fx-background-color: " + BG_CARD + ";-fx-background-radius: 10;");
        
        Label reglesTitle = new Label("📜 Règles de validation de l'année");
        reglesTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        reglesTitle.setTextFill(Color.web(COLOR_ACCENT));
        
        Label regle1 = new Label("• Moyenne générale ≥ 11/20");
        regle1.setTextFill(Color.web(TEXT_SECONDARY));
        Label regle2 = new Label("• Nombre de modules non validés ≤ 2");
        regle2.setTextFill(Color.web(TEXT_SECONDARY));
        Label regle3 = new Label("• Note de chaque module ≥ 6/20");
        regle3.setTextFill(Color.web(TEXT_SECONDARY));
        
        reglesBox.getChildren().addAll(reglesTitle, regle1, regle2, regle3);
        
        VBox content = new VBox(10, topBox, table, reglesBox);
        content.setPadding(new Insets(20));
        panel.getChildren().add(content);
        
        return panel;
    }
    
    //  PANNEAU STATISTIQUES 
    private Node panneauStatistiques() {
        VBox panel = createStyledPanel("📊 Statistiques et rapports");
        
        ComboBox<Promotion> comboPromo = createStyledComboBox(getPromotionsFiltrees());
        Button btnAfficher = createButton("📈 Afficher statistiques", COLOR_ACCENT);
        
        VBox statsContent = new VBox(15);
        statsContent.setPadding(new Insets(10, 0, 0, 0));
        
        btnAfficher.setOnAction(e -> {
            Promotion p = comboPromo.getValue();
            if (p == null) return;
            
            statsContent.getChildren().clear();
            
            List<Etudiant> etudiants = new ArrayList<>();
            for (Etudiant et : db.getEtudiantsByPromotion(p.getId())) {
                if (!et.isArchive() && db.calculerMoyenneGenerale(et.getId()) >= 0) etudiants.add(et);
            }
            etudiants.sort((a, b) -> Double.compare(db.calculerMoyenneGenerale(b.getId()), db.calculerMoyenneGenerale(a.getId())));
            
            if (etudiants.isEmpty()) {
                showAlert("Information", "Aucun étudiant actif dans cette promotion.", Alert.AlertType.INFORMATION);
                return;
            }
            
            Etudiant meilleur = etudiants.get(0);
            HBox bestBox = new HBox(10);
            bestBox.setAlignment(Pos.CENTER_LEFT);
            bestBox.setStyle("-fx-background-color: " + BG_CARD + ";-fx-background-radius: 10;-fx-padding: 15;");
            Label bestTitle = new Label("🌟 MEILLEUR ÉTUDIANT :");
            bestTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            bestTitle.setTextFill(Color.web(COLOR_WARNING));
            Label bestName = new Label(meilleur.getPrenom() + " " + meilleur.getNom() + " (" + String.format("%.2f", db.calculerMoyenneGenerale(meilleur.getId())) + "/20)");
            bestName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            bestName.setTextFill(Color.web(TEXT_PRIMARY));
            bestBox.getChildren().addAll(bestTitle, bestName);
            
            TableView<Object[]> rankTable = new TableView<>();
            TableColumn<Object[], String> colRank = new TableColumn<>("Rang");
            colRank.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[0]));
            colRank.setPrefWidth(60);
            TableColumn<Object[], String> colEtud = new TableColumn<>("Étudiant");
            colEtud.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[1]));
            colEtud.setPrefWidth(250);
            TableColumn<Object[], String> colMoy = new TableColumn<>("Moyenne / 20");
            colMoy.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[2]));
            colMoy.setPrefWidth(120);
            rankTable.getColumns().addAll(colRank, colEtud, colMoy);
            
            ObservableList<Object[]> rankData = FXCollections.observableArrayList();
            List<Object[]> validesData = new ArrayList<>();
            List<Object[]> nonValidesData = new ArrayList<>();
            
            List<Module> modules = db.getAllModules();
            for (int i = 0; i < etudiants.size(); i++) {
                Etudiant et = etudiants.get(i);
                double moy = db.calculerMoyenneGenerale(et.getId());
                rankData.add(new Object[]{String.valueOf(i+1), et.getPrenom() + " " + et.getNom(), String.format("%.2f", moy)});
                
                Object[] valRow = new Object[]{et.getPrenom() + " " + et.getNom(), String.format("%.2f", moy)};
                if (db.isValidationExistante(et.getId(), p.getId())) {
                    validesData.add(valRow);
                } else {
                    nonValidesData.add(valRow);
                }
            }
            rankTable.setItems(rankData);
            
            TableView<Object[]> valTable = new TableView<>();
            TableColumn<Object[], String> colValName = new TableColumn<>("✅ Validés (" + validesData.size() + ")");
            colValName.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[0]));
            colValName.setPrefWidth(180);
            TableColumn<Object[], String> colValMoy = new TableColumn<>("Moyenne");
            colValMoy.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[1]));
            colValMoy.setPrefWidth(80);
            valTable.getColumns().addAll(colValName, colValMoy);
            valTable.setItems(FXCollections.observableArrayList(validesData));
            
            TableView<Object[]> nonValTable = new TableView<>();
            TableColumn<Object[], String> colNonValName = new TableColumn<>("❌ Non Validés (" + nonValidesData.size() + ")");
            colNonValName.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[0]));
            colNonValName.setPrefWidth(180);
            TableColumn<Object[], String> colNonValMoy = new TableColumn<>("Moyenne");
            colNonValMoy.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[1]));
            colNonValMoy.setPrefWidth(80);
            nonValTable.getColumns().addAll(colNonValName, colNonValMoy);
            nonValTable.setItems(FXCollections.observableArrayList(nonValidesData));
            
            HBox valBox = new HBox(15, valTable, nonValTable);
            valBox.setAlignment(Pos.CENTER_LEFT);
            
            // ---------------- CHARTS ---------------- //
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Validés", validesData.size()),
                new PieChart.Data("Non Validés", nonValidesData.size())
            );
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Taux de Réussite");
            pieChart.setPrefHeight(250);
            pieChart.setPrefWidth(350);
            
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Mentions");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Nombre d'étudiants");
            yAxis.setTickUnit(1);
            yAxis.setMinorTickVisible(false);
            
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Répartition des Moyennes");
            barChart.setPrefHeight(250);
            barChart.setPrefWidth(450);
            barChart.setLegendVisible(false);
            
            int[] rep = new int[4];
            for (Object[] row : rankData) {
                double m = Double.parseDouble(((String)row[2]).replace(",", "."));
                if (m < 11) rep[0]++;
                else if (m < 13) rep[1]++;
                else if (m < 15) rep[2]++;
                else rep[3]++;
            }
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>("< 11", rep[0]));
            series.getData().add(new XYChart.Data<>("11-13", rep[1]));
            series.getData().add(new XYChart.Data<>("13-15", rep[2]));
            series.getData().add(new XYChart.Data<>(">= 15", rep[3]));
            barChart.getData().add(series);
            
            HBox chartsBox = new HBox(20, pieChart, barChart);
            chartsBox.setAlignment(Pos.CENTER);
            chartsBox.setStyle("-fx-background-color: " + BG_CARD + ";-fx-background-radius: 10;-fx-padding: 10;");
            
            Label rankTitle = new Label("🏆 Classement par moyenne générale :");
            rankTitle.setTextFill(Color.web(TEXT_SECONDARY));
            rankTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            
            Label valTitle = new Label("📋 Bilan des validations :");
            valTitle.setTextFill(Color.web(TEXT_SECONDARY));
            valTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            
            statsContent.getChildren().addAll(bestBox, chartsBox, rankTitle, rankTable, valTitle, valBox);
        });
        
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label promoTitleLabel = new Label("Promotion:");
        promoTitleLabel.setTextFill(Color.web(TEXT_PRIMARY));
        topBox.getChildren().addAll(promoTitleLabel, comboPromo, btnAfficher);
        topBox.setPadding(new Insets(10));
        
        ScrollPane scrollPane = new ScrollPane(statsContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent;");
        scrollPane.setPadding(new Insets(10));
        
        VBox content = new VBox(10, topBox, scrollPane);
        content.setPadding(new Insets(20));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        panel.getChildren().add(content);
        
        return panel;
    }
    
    //  PANNEAU NOTES PAR CLASSE 
    private Node panneauNotesClasse() {
        VBox panel = createStyledPanel("📊 Tableau récapitulatif des notes par classe");
        
        ComboBox<Promotion> comboPromo = createStyledComboBox(getPromotionsFiltrees());
        
        TableView<List<String>> table = new TableView<>();
        
        Button btnAfficher = createButton("📊 Afficher tableau", COLOR_ACCENT);
        
        btnAfficher.setOnAction(e -> {
            Promotion p = comboPromo.getValue();
            if (p == null) return;
            
            table.getColumns().clear();
            table.getItems().clear();
            
            List<Etudiant> etudiants = db.getEtudiantsByPromotion(p.getId());
            List<SousModule> sousMods = db.getAllSousModules();
            List<SousModule> promoSM = new ArrayList<>();
            for (SousModule sm : sousMods) {
                if (sm.getPromotion().getId() == p.getId()) {
                    promoSM.add(sm);
                }
            }
            
            if (etudiants.isEmpty()) {
                showAlert("Information", "Aucun étudiant dans cette promotion.", Alert.AlertType.INFORMATION);
                return;
            }
            
            TableColumn<List<String>, String> colNom = new TableColumn<>("Étudiant");
            colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
            colNom.setPrefWidth(200);
            table.getColumns().add(colNom);
            
            for (int i = 0; i < promoSM.size(); i++) {
                final int index = i + 1;
                TableColumn<List<String>, String> col = new TableColumn<>(promoSM.get(i).getNom());
                col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(index)));
                col.setPrefWidth(120);
                table.getColumns().add(col);
            }
            
            final int avgIndex = promoSM.size() + 1;
            TableColumn<List<String>, String> colAvg = new TableColumn<>("Moyenne / 20");
            colAvg.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(avgIndex)));
            colAvg.setPrefWidth(120);
            table.getColumns().add(colAvg);
            
            ObservableList<List<String>> rows = FXCollections.observableArrayList();
            for (Etudiant et : etudiants) {
                if(et.isArchive()) continue;
                List<String> row = new ArrayList<>();
                row.add(et.getPrenom() + " " + et.getNom());
                double somme = 0;
                int nbNotes = 0;
                for (SousModule sm : promoSM) {
                    double note = -1;
                    for (Note n : db.getNotesByEtudiant(et.getId())) {
                        if (n.getSousModule().getId() == sm.getId()) {
                            note = n.getNoteDefinitive();
                            break;
                        }
                    }
                    if (note >= 0) {
                        row.add(String.format("%.2f", note));
                        somme += note;
                        nbNotes++;
                    } else {
                        row.add("-");
                    }
                }
                double moyenne = nbNotes > 0 ? somme / nbNotes : 0;
                row.add(String.format("%.2f", moyenne));
                rows.add(row);
            }
            table.setItems(rows);
        });
        
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label promoTitleLabel = new Label("Promotion:");
        promoTitleLabel.setTextFill(Color.web(TEXT_PRIMARY));
        topBox.getChildren().addAll(promoTitleLabel, comboPromo, btnAfficher);
        topBox.setPadding(new Insets(10));
        
        VBox content = new VBox(10, topBox, table);
        content.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().add(content);
        
        return panel;
    }
    
    //  PANNEAU PROGRESSION ÉTUDIANT 
    private Node panneauProgressionEtudiant() {
        VBox panel = createStyledPanel("📊 Ma progression académique");
        
        TableView<Object[]> table = new TableView<>();
        
        TableColumn<Object[], String> colModule = new TableColumn<>("📚 Module");
        colModule.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[0]));
        colModule.setPrefWidth(300);
        
        TableColumn<Object[], String> colMoy = new TableColumn<>("📈 Moyenne / 20");
        colMoy.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[1]));
        colMoy.setPrefWidth(150);
        
        TableColumn<Object[], String> colStatut = new TableColumn<>("📌 Statut");
        colStatut.setCellValueFactory(d -> new SimpleStringProperty((String)d.getValue()[2]));
        colStatut.setPrefWidth(150);
        
        table.getColumns().addAll(colModule, colMoy, colStatut);
        
        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setStyle("-fx-background-color: " + BG_CARD + ";-fx-background-radius: 10;");
        bottomBox.setVisible(false);
        
        Button btnAfficher = createButton("📈 Voir ma progression", COLOR_ACCENT);
        
        btnAfficher.setOnAction(e -> {
            List<Module> modules = db.getAllModules();
            ObservableList<Object[]> rows = FXCollections.observableArrayList();
            
            for (Module m : modules) {
                double moyModule = db.calculerMoyenneParModule(etudiantConnecte.getId(), m.getId());
                boolean valide = ReglementEvaluation.estModuleValide(m, etudiantConnecte, db);
                String moyStr = moyModule > 0 ? String.format("%.2f", moyModule) : "-";
                String statutStr = moyModule > 0 ? (valide ? "✅ VALIDÉ" : "❌ NON VALIDÉ") : "📭 Aucune note";
                rows.add(new Object[]{m.getNom(), moyStr, statutStr});
            }
            table.setItems(rows);
            
            bottomBox.getChildren().clear();
            bottomBox.setVisible(true);
            
            double moyenneGenerale = db.calculerMoyenneGenerale(etudiantConnecte.getId());
            Label lblMoyenne = new Label(String.format("🎯 MOYENNE GÉNÉRALE : %.2f / 20", moyenneGenerale));
            lblMoyenne.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            lblMoyenne.setTextFill(Color.web(TEXT_PRIMARY));
            
            ReglementEvaluation.ResultatValidation result = ReglementEvaluation.validerAnnee(etudiantConnecte, modules, db);
            Label lblStatut = new Label("📌 STATUT DE L'ANNÉE : " + (result.valide ? "✅ VALIDÉE" : "❌ NON VALIDÉE"));
            lblStatut.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            lblStatut.setTextFill(Color.web(result.valide ? COLOR_SUCCESS : COLOR_DANGER));
            
            bottomBox.getChildren().addAll(lblMoyenne, lblStatut);
            
            if (!result.valide) {
                Label details = new Label(String.format("Détails : Moyenne générale (%.2f/20), Modules non validés (%d), Note < 6 détectée (%s)", 
                        result.moyenneGenerale, result.modulesNonValides, result.noteModuleInferieure6 ? "Oui" : "Non"));
                details.setTextFill(Color.web(TEXT_SECONDARY));
                bottomBox.getChildren().add(details);
            }
        });
        
        VBox content = new VBox(15, btnAfficher, table, bottomBox);
        content.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().add(content);
        
        return panel;
    }
    
    //  IMPORT/EXPORT 
    
    private void importerNotesExcel(ObservableList<NoteTableRow> data, SousModule sm) {
        if (sm == null) {
            showAlert("Information", "Veuillez sélectionner un sous-module", Alert.AlertType.WARNING);
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer des notes depuis Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(primaryStage);
        
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook(fis)) {
                
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
                int count = 0;
                
                for (org.apache.poi.ss.usermodel.Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    
                    String nomEtudiant = "";
                    double note = -1;
                    double rattrapage = -1;
                    
                    org.apache.poi.ss.usermodel.Cell nomCell = row.getCell(0);
                    if (nomCell != null) {
                        if (nomCell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                            nomEtudiant = nomCell.getStringCellValue().trim();
                        } else if (nomCell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                            nomEtudiant = String.valueOf((int) nomCell.getNumericCellValue());
                        }
                    }
                    
                    org.apache.poi.ss.usermodel.Cell noteCell = row.getCell(1);
                    if (noteCell != null) {
                        if (noteCell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                            note = noteCell.getNumericCellValue();
                        } else if (noteCell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                            try { note = Double.parseDouble(noteCell.getStringCellValue()); } catch (NumberFormatException ignored) {}
                        }
                    }
                    
                    org.apache.poi.ss.usermodel.Cell rattrapageCell = row.getCell(2);
                    if (rattrapageCell != null) {
                        if (rattrapageCell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                            rattrapage = rattrapageCell.getNumericCellValue();
                        } else if (rattrapageCell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                            try { rattrapage = Double.parseDouble(rattrapageCell.getStringCellValue()); } catch (NumberFormatException ignored) {}
                        }
                    }
                    
                    for (NoteTableRow noteRow : data) {
                        if (noteRow.getEtudiantNom().equalsIgnoreCase(nomEtudiant)) {
                            if (note >= 0) {
                                noteRow.setNote(note);
                                if (note >= 11) noteRow.setNoteRattrapage(-1);
                            }
                            if (rattrapage >= 0 && noteRow.getNote() < 11) {
                                noteRow.setNoteRattrapage(rattrapage);
                            }
                            Etudiant et = db.getEtudiantById(noteRow.getId());
                            if (et != null) {
                                db.saveNote(new Note(et, sm, noteRow.getNote(), noteRow.getNoteRattrapage()));
                            }
                            count++;
                            break;
                        }
                    }
                }
                
                hasUnsavedChanges = false;
                updateLampeIndicateur();
                
                showAlert("Succès", count + " notes importées et sauvegardées avec succès !", Alert.AlertType.INFORMATION);
                
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de l'import : " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
    
    private void exporterNotesExcel(ObservableList<NoteTableRow> data, SousModule sm) {
        if (sm == null) {
            showAlert("Information", "Veuillez sélectionner un sous-module", Alert.AlertType.WARNING);
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les notes vers Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        String fileName = "notes_" + sm.getNom().replaceAll(" ", "_") + ".xlsx";
        fileChooser.setInitialFileName(fileName);
        
        File file = fileChooser.showSaveDialog(primaryStage);
        
        if (file != null) {
            String path = file.getPath();
            if (!path.toLowerCase().endsWith(".xlsx")) {
                file = new File(path + ".xlsx");
            }
            
            try {
                org.apache.poi.ss.usermodel.Workbook workbook = new XSSFWorkbook();
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Notes");
                
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Étudiant");
                headerRow.createCell(1).setCellValue("Note Initiale /20");
                headerRow.createCell(2).setCellValue("Rattrapage /20");
                
                int rowNum = 1;
                for (NoteTableRow row : data) {
                    org.apache.poi.ss.usermodel.Row excelRow = sheet.createRow(rowNum++);
                    excelRow.createCell(0).setCellValue(row.getEtudiantNom());
                    if (row.getNote() >= 0) excelRow.createCell(1).setCellValue(row.getNote());
                    if (row.getNoteRattrapage() >= 0) excelRow.createCell(2).setCellValue(row.getNoteRattrapage());
                }
                
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);
                
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                    fos.flush();
                }
                workbook.close();
                
                if (file.exists() && file.length() > 0) {
                    showAlert("Succès", "✅ Fichier exporté avec succès !\n\n" +
                              "Nom: " + file.getName() + "\n" +
                              "Emplacement: " + file.getParent(), 
                              Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Le fichier n'a pas été créé correctement.", Alert.AlertType.ERROR);
                }
                
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de l'export : " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
    
    //  MÉTHODES UTILITAIRES 
    private void updateLampeIndicateur() {
        if (lampeIndicateur != null) {
            if (hasUnsavedChanges) {
                lampeIndicateur.setTextFill(Color.web(COLOR_DANGER));
            } else {
                lampeIndicateur.setTextFill(Color.web(COLOR_SUCCESS));
            }
        }
    }
    
    private VBox createStyledPanel(String title) {
        VBox panel = new VBox();
        panel.setStyle("-fx-background-color: " + BG_SECONDARY + ";-fx-background-radius: 10;");
        
        DropShadow panelShadow = new DropShadow();
        panelShadow.setColor(Color.web("#00000044"));
        panelShadow.setRadius(15);
        panelShadow.setOffsetY(5);
        panel.setEffect(panelShadow);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web(COLOR_ACCENT_LIGHT));
        titleLabel.setPadding(new Insets(15, 20, 10, 20));
        
        panel.getChildren().add(titleLabel);
        return panel;
    }
    
    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "dd;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 8;-fx-padding: 8 18 8 18;");
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + color + ";-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 8;-fx-padding: 8 18 8 18;");
            btn.setEffect(new Glow(0.3));
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: " + color + "dd;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 8;-fx-padding: 8 18 8 18;");
            btn.setEffect(null);
        });
        return btn;
    }
    
    //  CLASSES INTERNES 
    public static class NoteTableRow {
        private final int id;
        private final String etudiantNom;
        private double note;
        private double noteRattrapage;
        
        public NoteTableRow(int id, String etudiantNom, double note, double noteRattrapage) {
            this.id = id;
            this.etudiantNom = etudiantNom;
            this.note = note;
            this.noteRattrapage = noteRattrapage;
        }
        public int getId() { return id; }
        public String getEtudiantNom() { return etudiantNom; }
        public double getNote() { return note; }
        public void setNote(double note) { this.note = note; }
        public double getNoteRattrapage() { return noteRattrapage; }
        public void setNoteRattrapage(double noteRattrapage) { this.noteRattrapage = noteRattrapage; }
    }
    
    public static class ValidationRow {
        private final int id;
        private final String nom;
        private final double moyenneGenerale;
        private final int modulesNonValides;
        private final String noteInferieure6;
        private final String valide;
        
        public ValidationRow(int id, String nom, double moyenneGenerale, int modulesNonValides, String noteInferieure6, String valide) {
            this.id = id;
            this.nom = nom;
            this.moyenneGenerale = moyenneGenerale;
            this.modulesNonValides = modulesNonValides;
            this.noteInferieure6 = noteInferieure6;
            this.valide = valide;
        }
        public int getId() { return id; }
        public String getNom() { return nom; }
        public double getMoyenneGenerale() { return moyenneGenerale; }
        public int getModulesNonValides() { return modulesNonValides; }
        public String getNoteInferieure6() { return noteInferieure6; }
        public String getValide() { return valide; }
    }
    
    public static class ValidationRowSimple {
        private final int id;
        private final String nom;
        private final double moyenneGenerale;
        private final double moyenneModules;
        private final String peutPasser;
        private final String valide;
        
        public ValidationRowSimple(int id, String nom, double moyenneGenerale, double moyenneModules, String peutPasser, String valide) {
            this.id = id;
            this.nom = nom;
            this.moyenneGenerale = moyenneGenerale;
            this.moyenneModules = moyenneModules;
            this.peutPasser = peutPasser;
            this.valide = valide;
        }
        public int getId() { return id; }
        public String getNom() { return nom; }
        public double getMoyenneGenerale() { return moyenneGenerale; }
        public double getMoyenneModules() { return moyenneModules; }
        public String getPeutPasser() { return peutPasser; }
        public String getValide() { return valide; }
    }
    

}
