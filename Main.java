import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        String csvPath = "Insert path here";

        int batchSize = 20;

        Connection connection = null;

        ICsvBeanReader beanReader = null;
        CellProcessor[] processors = new CellProcessor[] {
            new NotNull(), //Poster_Link
            new NotNull(), //Series_Title
            new ParseMovieYear(), //Released_Year
            new Optional(), //Certificate
            new ParseMovieIntegerFormat(), //Runtime
            new NotNull(), //Genre
            new ParseDouble(), //IMDB_Rating
            new NotNull(), //Overview
            new Optional(new ParseInt()), //Meta_score
            new NotNull(), //Director
            new NotNull(), //Star1
            new NotNull(), //Star2
            new NotNull(), //Star3
            new NotNull(), //Star4
            new ParseInt(), //No_of_votes
            new Optional(new ParseMovieIntegerFormat()) //Gross
        };

        try {
            beanReader = new CsvBeanReader(new FileReader(csvPath), CsvPreference.STANDARD_PREFERENCE);
            beanReader.getHeader(true);

            String[] header = {"Poster_Link", "Series_Title", "Released_Year", "Certificate", "Runtime", "Genre", "IMDB_Rating", "Overview", "Meta_score", "Director", "Star1", "Star2", "Star3", "Star4", "No_of_Votes", "Gross"};
            Filme bean = null;

            //

            int count = 0;

            while ((bean = beanReader.read(Filme.class, header, processors)) != null) {
                String titulo = bean.getSeries_Title();
                Integer ano = bean.getReleased_Year();
                String certificado = bean.getCertificate();
                Integer tempo = bean.getRuntime();
                String genero = bean.getGenre();
                Double avaliacaoIMDB = bean.getIMDB_Rating();
                Integer metascore = bean.getMeta_score();
                String diretor = bean.getDirector();
                String estrela1 = bean.getStar1();
                String estrela2 = bean.getStar2();
                String estrela3 = bean.getStar3();
                String estrela4 = bean.getStar4();
                Integer votos = bean.getNo_of_Votes();
                Integer receita = bean.getGross();

                //

                count++;
                if (count == 20) {
                    break;
                    //count = 0;
                }
            }
            beanReader.close();

            //


        } catch (IOException e) {
            System.out.println(e);
        }
    }
}