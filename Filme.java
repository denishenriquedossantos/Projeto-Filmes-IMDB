public class Filme {
    private String Poster_Link;
    private String Series_Title;
    private Integer Released_Year;
    private String Certificate;
    private Integer Runtime;
    private String Genre;
    private Double IMDB_Rating;
    private String Overview;
    private Integer Meta_score;
    private String Director;
    private String Star1;
    private String Star2;
    private String Star3;
    private String Star4;
    private Integer No_of_Votes;
    private Integer Gross;

    public Filme() {}

    public Filme(String Poster_Link, String Series_Title, Integer Released_Year, String Certificate, Integer Runtime, String Genre, Double IMDB_Rating, String Overview, Integer Meta_score, String Director, String Star1, String Star2, String estrela3, String Star4, Integer No_of_Votes, Integer Gross) {
        this.Poster_Link = Poster_Link;
        this.Series_Title = Series_Title;
        this.Released_Year = Released_Year;
        this.Certificate = Certificate;
        this.Runtime = Runtime;
        this.Genre = Genre;
        this.IMDB_Rating = IMDB_Rating;
        this.Overview = Overview;
        this.Meta_score = Meta_score;
        this.Director = Director;
        this.Star1 = Star1;
        this.Star2 = Star2;
        this.Star3 = estrela3;
        this.Star4 = Star4;
        this.No_of_Votes = No_of_Votes;
        this.Gross = Gross;
    }

    public String getPoster_Link() {
        return Poster_Link;
    }

    public void setPoster_Link(String poster_Link) {
        this.Poster_Link = poster_Link;
    }

    public String getSeries_Title() {
        return Series_Title;
    }

    public void setSeries_Title(String series_Title) {
        this.Series_Title = series_Title;
    }

    public Integer getReleased_Year() {
        return Released_Year;
    }

    public void setReleased_Year(Integer released_Year) {
        this.Released_Year = released_Year;
    }

    public String getCertificate() {
        return Certificate;
    }

    public void setCertificate(String certificate) {
        this.Certificate = certificate;
    }

    public Integer getRuntime() {
        return Runtime;
    }

    public void setRuntime(Integer runtime) {
        this.Runtime = runtime;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        this.Genre = genre;
    }

    public Double getIMDB_Rating() {
        return IMDB_Rating;
    }

    public void setIMDB_Rating(Double IMDB_Rating) {
        this.IMDB_Rating = IMDB_Rating;
    }

    public String getOverview() {
        return Overview;
    }

    public void setOverview(String overview) {
        this.Overview = overview;
    }

    public Integer getMeta_score() {
        return Meta_score;
    }

    public void setMeta_score(Integer meta_score) {
        this.Meta_score = meta_score;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        this.Director = director;
    }

    public String getStar1() {
        return Star1;
    }

    public void setStar1(String star1) {
        this.Star1 = star1;
    }

    public String getStar2() {
        return Star2;
    }

    public void setStar2(String star2) {
        this.Star2 = star2;
    }

    public String getStar3() {
        return Star3;
    }

    public void setStar3(String star3) {
        this.Star3 = star3;
    }

    public String getStar4() {
        return Star4;
    }

    public void setStar4(String star4) {
        this.Star4 = star4;
    }

    public Integer getNo_of_Votes() {
        return No_of_Votes;
    }

    public void setNo_of_Votes(Integer no_of_Votes) {
        this.No_of_Votes = no_of_Votes;
    }

    public Integer getGross() {
        return Gross;
    }

    public void setGross(Integer gross) {
        this.Gross = gross;
    }
}
