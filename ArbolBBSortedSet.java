package es.ubu.inf.edat.pr05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;

import es.ubu.inf.edat.datos.GeneradorEnteros;

/**
 * 
 * @author Jorge Vara Rodriguez
 *
 * @version v1
 * @param <E> parametro generico
 */

public class ArbolBBSortedSet<E> extends ArbolBB<E> implements SortedSet<E> {

	/** Comparador */
	private Comparator<? super E> comparator;

	/**
	 * Constructor por defecto
	 */
	public ArbolBBSortedSet() {
		super();
	}

	/**
	 * Constructor que incluye una coleccion para inicializar el Set
	 * 
	 * @param coleccion que se incluye en el set en el momento de su instanciacion
	 *                  No se incluiran los elementos duplicados
	 */
	public ArbolBBSortedSet(Collection<? extends E> coleccion) {
		super(coleccion);

	}

	/**
	 * Comparador
	 * 
	 * @param c comparador
	 */
	public ArbolBBSortedSet(Comparator<? super E> c) {
		super(c);
		this.comparator = c;
	}

	/**
	 * Constructor que incluye una coleccion para inicializar el Set
	 * 
	 * @param coleccion que se incluye en el set en el momento de su instanciacion
	 *                  No se incluiran los elementos duplicados
	 * @param c         comparador empleado para determinar el orden de los
	 *                  elementos en el Set
	 */
	public ArbolBBSortedSet(Collection<? extends E> coleccion, Comparator<? super E> c) {
		super(coleccion, c);
	}

	/**
	 * Comprueba si existe un elemento
	 * 
	 * @param elemento elemento a comprobar
	 * @return true si existe, false en caso contrario
	 */
	private boolean existeElemento(E elemento) {
		List<E> lista = new ArrayList<>();
		inOrdenRecursivo(raiz, lista);
		return lista.contains(elemento) ? true : false;
	}

	/**
	 * Permitirá conocer la profundidad dentro del árbol a la que se encuentra el
	 * elemento solicitado
	 * 
	 * @param elemento elemento
	 * @return profundidad o -1 si no existe el elemento
	 */
	public int profundidad(E elemento) {
		int cont = 0;
		Nodo actual = raiz;
		if (!existeElemento(elemento)) {
			return -1;
		}
		while (!actual.getDato().equals(elemento)) {
			actual = comparar(elemento, actual.getDato()) < 0 ? actual.getIzq() : actual.getDer();
			cont++;
		}
		return cont;
	}

	/**
	 * Devuelve una lista con los nodos hoja da partir del nodo pasado
	 * 
	 * @param reco nodo a partir del que se obtendran los nodos hoja
	 * @return lista de nodos hoja
	 */
	private List<Nodo> NodosHoja(Nodo reco) {
		List<Nodo> lista = new ArrayList<>();
		if (reco != null) {
			if (reco.getIzq() == null && reco.getDer() == null) {
				lista.add(reco);
			}
			if (reco.getIzq() != null || reco.getDer() != null) {
				lista.addAll(NodosHoja(reco.getIzq()));
				lista.addAll(NodosHoja(reco.getDer()));
			}
		}
		return lista;
	}

	/**
	 * Permitirá conocer la altura dentro del árbol a la que se encuentra el
	 * elemento solicitado
	 * 
	 * @param elemento elemento
	 * @return altura o -1 si no existe el elemento
	 */
	public int altura(E elemento) {
		int max = 0;
		int profundidad = 0;
		if (!existeElemento(elemento)) {
			return -1;
		}
		for (Nodo e : NodosHoja(buscar(raiz, elemento).get(0))) {
			profundidad = profundidad(e.getDato());
			max = profundidad > max ? profundidad : max;
		}
		return max - profundidad(elemento);
	}

	/**
	 * Generar un árbol binario de búsqueda a partir de preorder e inorder.
	 * 
	 * @param preorder preorden
	 * @param inorder  inorden
	 * @return siguiente raiz
	 */
	public E reconstruyeArbol(List<E> preorder, List<E> inorder) {
		if (preorder.size() == 0) {
			return null;
		} else if (preorder.size() == 1) {
			return preorder.get(0);
		}
		E actual = preorder.get(0);
		add(actual); // Añade la raiz actual

		int i = 0; // Posicion de la raiz actual en inorden
		while (inorder.get(i) != actual) {
			i++;
		}

		E izq = reconstruyeArbol(preorder.subList(1, i + 1), inorder.subList(0, i));
		if (izq != null) {
			add(izq);
		}
		E drch = reconstruyeArbol(preorder.subList(i + 1, preorder.size()), inorder.subList(i + 1, inorder.size()));
		if (drch != null) {
			add(drch);
		}
		return actual;
	}

	/**
	 * Devuelve un rango entre los elementos pasados por parametro
	 * 
	 * @param fromElement primer elemento del rango (incluido)
	 * @param toElement   ultimo elemento del rango (no incluido)
	 * @return Vista del rango de elementos
	 */
	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		SortedSet<E> set = new ArbolBBSortedSet<E>();
		boolean flag = false; // Si se ha llegado a fromElement o no
		Nodo actual = raiz;
		Stack<Nodo> pila = new Stack<>();

		// Comprueba si el orden de los parametros es correcto o si son iguales
		if (comparar(fromElement, toElement) >= 0) {
			return set;
		}
		while (!pila.isEmpty() || actual != null) {
			while (actual != null) {
				pila.push(actual);
				actual = actual.getIzq();
			}
			if (!pila.isEmpty()) {
				Nodo temp = pila.pop();
				// Comprueba si se ha llegado a toElement
				if (temp.getDato().equals(toElement) && flag) {
					break;
				}
				//// Comprueba si se ha llegado a fromElement
				if (temp.getDato().equals(fromElement)) {
					flag = true;
				}
				// En caso de estar en el rango
				if (flag) {
					set.add(temp.getDato());
				}
				actual = temp.getDer();
			}
		}
		return set;
	}

	/**
	 * Devuelve un rango de elementos hasta el pasado por parametro (no incluido)
	 * 
	 * @param toElement ultimo elemento del rango (no incluido)
	 * @return Vista del rango de elementos
	 */
	@Override
	public SortedSet<E> headSet(E toElement) {
		SortedSet<E> set = new ArbolBBSortedSet<E>();
		Nodo actual = raiz;
		Stack<Nodo> pila = new Stack<>();
		while (!pila.isEmpty() || actual != null) {
			while (actual != null) {
				pila.push(actual);
				actual = actual.getIzq();
			}
			if (!pila.isEmpty()) {
				Nodo temp = pila.pop();
				// Comprueba si se ha llegadoa toElement
				if (temp.getDato().equals(toElement)) {
					break;
				}
				set.add(temp.getDato()); // Añade los valores
				actual = temp.getDer();
			}
		}
		return set;
	}

	/**
	 * Devuelve un rango de elementos desde el pasado por parametros hasta el final
	 * 
	 * @param fromElement primer elemento del rango (incluido)
	 * @return Vista del rango de elementos
	 */
	@Override
	public SortedSet<E> tailSet(E fromElement) {
		SortedSet<E> set = new ArbolBBSortedSet<E>();
		boolean flag = false; // Si se ha llegado a fromElement o no
		Nodo actual = raiz;
		Stack<Nodo> pila = new Stack<>();
		while (!pila.isEmpty() || actual != null) {
			while (actual != null) {
				pila.push(actual);
				actual = actual.getIzq();
			}
			if (!pila.isEmpty()) {
				Nodo temp = pila.pop();
				// Comprueba si se ha llegado a fromElement
				if (temp.getDato().equals(fromElement)) {
					flag = true;
				}
				// Si se ha llegado a fromElement
				if (flag) {
					set.add(temp.getDato());
				}
				actual = temp.getDer();
			}
		}
		return set;
	}

	/**
	 * Primer elemento del arbol
	 * 
	 * @return elemento
	 */
	@Override
	public E first() {
		return menor(raiz).getDato();
	}

	/**
	 * Ultimo elemento del arbol
	 * 
	 * @return elemento
	 */
	@Override
	public E last() {
		return mayor(raiz).getDato();
	}

	/**
	 * Comparador
	 * 
	 * @return comparador
	 */
	@Override
	public Comparator<? super E> comparator() {
		return this.comparator;
	}

}
