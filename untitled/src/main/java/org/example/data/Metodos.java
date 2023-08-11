package org.example.data;

import org.example.model.Participante;
import org.example.model.ParticipanteSala;
import org.example.model.Sala;
import org.example.model.SalaPorcentaje;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Metodos {
    //Crear un método para dar de alta una sala, el nombre no puede ser vacío y la capacidad máxima no
    //puede ser <= 0.
    public void altaSala(String nombre, int capacidad_maxima) {
        try(Session session = HibernateUtil.getSession())
        {
            if (!(nombre == "" || capacidad_maxima <= 0))
            {
                session.beginTransaction();
                Sala sala = new Sala();
                sala.setNombre(nombre);
                sala.setCapacidad_maxima(capacidad_maxima);
                session.save(sala);
                session.getTransaction().commit();
                System.out.println("Alta exitosa");
            }
            else {
                System.out.println("Error");
            }
        }
    }
    //Crear un método para actualizar una sala, aplicando las mismas validaciones que en la creación.
    public void updateSala(String nombre,String nombreNuevo, int capacidad_maxima)
    {
        try(Session session = HibernateUtil.getSession())
        {
            if (!(nombre == "" || capacidad_maxima <= 0))
            {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Sala> criteria = builder.createQuery(Sala.class);
                Root<Sala> root = criteria.from(Sala.class);
                criteria.select(root).where(builder.equal(root.get("nombre"), nombre));
                Sala sala = session.createQuery(criteria).getSingleResult();
                session.beginTransaction();
                sala.setCapacidad_maxima(capacidad_maxima);
                sala.setNombre(nombreNuevo);
                session.update(sala);
                session.getTransaction().commit();
                System.out.println("Update exitoso");
            }
            else {
                System.out.println("Error");
            }
        }
    }
    //Crear un método para añadir un participante nuevo a una sala, el nombre y la sala no pueden ser
    //nulos.
    public void newParticipante(String nombreSala, String nombreParticipante)
    {
        try(Session session =HibernateUtil.getSession())
        {
            if (!(nombreSala == "" || nombreParticipante == ""))
            {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Sala> criteria = builder.createQuery(Sala.class);
                Root<Sala> root = criteria.from(Sala.class);
                criteria.select(root).where(builder.equal(root.get("nombre"), nombreSala));
                Sala sala = session.createQuery(criteria).getSingleResult();
                List<Participante> participantes = sala.getParticipantes();
                if(participantes.size() < sala.getCapacidad_maxima())
                {
                    Participante participante = new Participante();
                    participante.setNombre(nombreParticipante);
                    participante.setSala_asignada(session.createQuery(criteria).getSingleResult().getId());
                    session.beginTransaction();
                    session.save(participante);
                    session.getTransaction().commit();
                }
                else
                {
                    System.out.println("La sala esta llena");
                }
                System.out.println("Participante agregado");
            }
            else {
                System.out.println("Error");
            }
        }
    }

    //Crear un método para eliminar un participante de una sala.
    public void deleteParticipanteSala(String nombreParticipante, String nombreSala)
    {
        try(Session session = HibernateUtil.getSession())
        {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Sala> criteria = builder.createQuery(Sala.class);
            Root<Sala> root = criteria.from(Sala.class);
            criteria.select(root).where(builder.equal(root.get("nombre"), nombreSala));
            Sala sala = session.createQuery(criteria).getSingleResult();
            List<Participante> participantes = sala.getParticipantes();
            for(Participante participante : participantes)
            {
                if(participante.getNombre().equals(nombreParticipante))
                {
                    session.beginTransaction();
                    session.delete(participante);
                    session.getTransaction().commit();
                }
            }
        }
    }

    //Crear un método que retorne una lista de objetos con: Nombre Sala | Porcentaje ocupación
    public List<SalaPorcentaje> getSalaPorcentajes()
    {
        try(Session session = HibernateUtil.getSession())
        {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Sala> criteria = builder.createQuery(Sala.class);
            Root<Sala> root = criteria.from(Sala.class);
            criteria.select(root);
            List<Sala> salas = session.createQuery(criteria).getResultList();
            List<SalaPorcentaje> salaPorcentajes = new ArrayList<>();
            for(Sala sala : salas)
            {
                SalaPorcentaje salaPorcentaje = new SalaPorcentaje();
                salaPorcentaje.setNombreSala(sala.getNombre());
                salaPorcentaje.setPorcentaje(((double) sala.getParticipantes().size() / sala.getCapacidad_maxima())*100);
                salaPorcentajes.add(salaPorcentaje);
            }
            return salaPorcentajes;
        }
    }

    //Crear un método que retorne una lista de objetos con: Nombre Participante | Nombre Sala. Este
    //método debe poder filtrar la lista de participantes por nombre (por aproximación).
    public List<ParticipanteSala> getParticipanteSala(String inicial)
    {
        try(Session session = HibernateUtil.getSession())
        {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Participante> criteria = builder.createQuery(Participante.class);
            Root<Participante> root = criteria.from(Participante.class);
            criteria.select(root).where(builder.like(root.get("nombre"), inicial + "%"));
            List<Participante> participantes = session.createQuery(criteria).getResultList();
            List<ParticipanteSala> participanteSalas = new ArrayList<>();
            for(Participante participante : participantes)
            {
                ParticipanteSala participanteSala = new ParticipanteSala();
                participanteSala.setNombreParticipante(participante.getNombre());
                CriteriaQuery<Sala> criteriaSala = builder.createQuery(Sala.class);
                Root<Sala> rootSala = criteriaSala.from(Sala.class);
                criteriaSala.select(rootSala).where(builder.equal(rootSala.get("id"), participante.getSala_asignada()));
                Sala sala = session.createQuery(criteriaSala).getSingleResult();
                participanteSala.setNombreSala(sala.getNombre());
                participanteSalas.add(participanteSala);
            }
            return participanteSalas;
        }
    }
}
