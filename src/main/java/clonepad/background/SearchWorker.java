package clonepad.background;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchWorker extends SwingWorker<List<SearchWorker.MatchIndexes>, Void> {
    final JTextArea textArea;
    final JTextField textField;
    final Boolean isUseRegex;
    private Consumer<List<MatchIndexes>> setResults;


    public SearchWorker(JTextArea textArea, JTextField textField, Boolean isUseRegex,
                        Consumer<List<MatchIndexes>> setResults) {
        this.isUseRegex = isUseRegex;
        this.setResults = setResults;
        this.textArea = textArea;
        this.textField = textField;
    }

    @Override
    protected List<MatchIndexes> doInBackground() {
        return new RegexSearcher(textField.getText()).calculate();
    }

    @Override
    protected void done() {
        try {

            setResults.accept(get());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class RegexSearcher {
        String regex;
        Pattern pattern;
        Matcher matcher;

        public RegexSearcher(String regex) {
            this.regex = regex;
            if(isUseRegex) {
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(regex, Pattern.LITERAL);
            }
            matcher = pattern.matcher(textArea.getText());
        }

        public List<MatchIndexes> calculate() {
            List<MatchIndexes> results = new ArrayList<>();
            while(matcher.find()) {
                MatchIndexes cur = new MatchIndexes(matcher.start(), matcher.end());
                System.err.println(cur.endIndex + " " + cur.startIndex);
                results.add(cur);
            }
            return results;
        }
    }
    public class MatchIndexes {
        public Integer startIndex;
        public Integer endIndex;

        public MatchIndexes(Integer startIndex, Integer endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }
}
