A package containing various abstract classes and utility objects shared across multiple IBM (Individual-Based Model) implementations.

This package does not include any standalone "models." Instead, it serves as a foundation, allowing users to develop their own models by extending the classes or implementing the interfaces provided.

One example is the AbstractIndividualInterface. As a Java interface, it cannot be instantiated directly as an object. However, users can define their own classes that implement AbstractIndividualInterface, enabling the reuse of its parameters and functions in other models or applications where the interface is utilized.

For instance, nearly all IBM implementations use the getId() function from this interface to retrieve a unique identifier for an individual. A typical individual might be defined as follows:
```
class MyPerson implements AbstractIndividualInterface {

    // Fields and other class-specific attributes

    @Override
    public int getId() {
        // Return unique identifier
    }

    // Additional methods specific to AbstractIndividualInterface or MyPerson
}
```
By implementing this interface, objects can be shared and interchanged seamlessly between different IBMs. For example, you could define a function to find the maximum ID in a population as follows:

```
public int getMaxId(AbstractIndividualInterface[] population) {
    int maxId = -1;
    for (AbstractIndividualInterface individual : population) {
        maxId = Math.max(individual.getId(), maxId);
    }
    return maxId;
}
```

This function will work even if the population array contains a mixture of MyPerson objects and other types of individuals, as long as they all implement the AbstractIndividualInterface.

The number of objects in this package is too extensive to describe fully in the README file, and users are encouraged to explore specific components individually to assess their relevance to their needs. However, some of the key files include:

* AbstractIndividualInterface: For modeling an individual.
* AbstractInfection: For modeling typical infections.
* AbstractAvailability: For modeling the formation of partnerships.
* AbstractPopulation: For representing the population as a whole. Notably, its extension, AbstractFieldsArrayPopulation, includes methods for easy access to fields and parameters within models.
* ContactMap and RelationShipMap: For defining partnerships between individuals in the model. These are represented as simple graphs and can be used independently, provided the edges are properly defined.

Other useful object includes:
* SimulationInterface: For managing and running simulations.
* Miscellaneous utility files in the util package, such as tools for I/O management, PersonClassifier, and utilities for reading properties files.

Users are encouraged to refer to the documentation and source code for a deeper understanding of each object’s functionality and usage.
