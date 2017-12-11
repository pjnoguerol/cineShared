package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import com.cineshared.pjnogegonzalez.cineshared.peliculas.Peliculas;

import java.io.Serializable;
import java.util.List;

/**
 * Clase FindApiBusqueda con todos los atributos de una búsqueda y los métodos getter y setter
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class FindApiBusqueda implements Serializable {

    private int page;
    private int total_results;
    private int total_pages;
    private List<Peliculas> results;

    // Métodos getter y setter de todos los atributos
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Peliculas> getResults() {
        return results;
    }

    public void setResults(List<Peliculas> results) {
        this.results = results;
    }

}
