import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.*;
import me.gosimple.nbvcxz.scoring.Result;
import me.gosimple.nbvcxz.scoring.TimeEstimate;

import java.util.List;
import java.util.Locale;

class  User {
    private String firstName;
    private String lastName;
    private String email;

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

public class Zxcvbn {

    public static void main(String[] args) {
        User user = new User();
        user.setFirstName("mbengue");
        user.setLastName("malick");
        user.setEmail("malick@gmail.com");

        // Creer un dictionnaire sur lequel on ajoute les informations de l'utilisateur pour lui interdire d'utiliser son nom ou son email ou son prénom comme mot de passe
        List<Dictionary> dictionaryList = ConfigurationBuilder.getDefaultDictionaries();
        dictionaryList.add(new DictionaryBuilder()
                .setDictionaryName("exclude")
                .setExclusion(true)
                .addWord(user.getFirstName(), 0)
                .addWord(user.getLastName(), 0)
                .addWord(user.getEmail(), 0)
                .createDictionary());

        // Configuration de la politique du mot de passe
        // La force du mot de passe sera vérifiée en fonction de l'entropie c-à-d le nombre de répétition d'un caractere
        // Si la somme de répétition de tous les caracteres est inférieure à la valeur fixée ici (40). on considere que le mot de
        // passe n'a pas respecter la politique établie
        Configuration configuration = new ConfigurationBuilder()
                .setLocale(Locale.forLanguageTag("fr"))
                .setMinimumEntropy(40d)
                .setDictionaries(dictionaryList)
                .createConfiguration();

        Nbvcxz nbvcxz = new Nbvcxz(configuration);
        // Genere un mot de passe de (2) mot separé par un delimiteur
        // String pass1 = Generator.generatePassphrase("$",2);
        // Genere un mot de passe alphanumeric de (8) caracteres
        //String pass = Generator.generateRandomPassword(Generator.CharacterTypes.ALPHANUMERIC, 8);
        //System.out.println("Password: "+pass);

        // ici pour tester la politique je vais lui passer l'adresse mail que je ve me connecter plutard
        // Ce qui normalement ne passera pas
        Result result = nbvcxz.estimate("malick@gmail.com");
        // Afficher le score qui varie de 0-4 selon la force du mot de passe saisi
        // Ici le score sera egale à 0 car l'adresse mail faisant des mots de passe exclus qu'il ne faut pas utiliser
        System.out.println("score: "+result.getBasicScore());;

        // Calculer ou estimer le temps qu'il faudra pour casser le mot de passe en ligne et hors ligne
        String timeToCrackOff = TimeEstimate.getTimeToCrackFormatted(result, "OFFLINE_BCRYPT_12");
        String timeToCrackOn = TimeEstimate.getTimeToCrackFormatted(result, "ONLINE_THROTTLED");

        // Vérifiez si le mot de passe correspond au minimum défini dans la configuration
        if(result.isMinimumEntropyMet())
        {

            StringBuilder successMessage = new StringBuilder();
            successMessage.append("Le mot de passe a satisfait aux exigences minimales de résistance.");
            successMessage.append("\nTemps pour craquer le mot de passe - en ligne: ").append(timeToCrackOn);
            successMessage.append("\nTemps pour craquer le mot de passe - hors ligne: ").append(timeToCrackOff);

            // Ci dessus ne sont que des exemple d'affichage pas trop nécessaire a afficher à l'utilisateur sur la partie front
            System.out.println(successMessage.toString());

        }
        else
        {
            // Si le mot de passe n'est pas conforme suggerer à l'utilisateur de changer sont mot de passe
            Feedback feedback = result.getFeedback();

            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Le mot de passe n'a pas satisfait aux exigences minimales de résistance.");
            errorMessage.append("\nTemps pour craquer le mot de passe - en ligne: ").append(timeToCrackOn);
            errorMessage.append("\nTemps pour craquer le mot de passe - hors ligne: ").append(timeToCrackOff);

            if(feedback != null)
            {
                if (feedback.getWarning() != null)
                    errorMessage.append("\nAttention: ").append(feedback.getWarning());
                for (String suggestion : feedback.getSuggestion())
                {
                    errorMessage.append("\nSuggestion: ").append(suggestion);
                }
            }

            System.out.println(errorMessage.toString());
        }
    }
}
