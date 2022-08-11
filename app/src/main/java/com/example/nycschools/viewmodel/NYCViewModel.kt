package com.example.nycschools.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nycschools.model.Repository

/**
 * DI (Dependency Injections)
 * 2 types of injections
 * constructor injection.- Create the object along with the needed dependencies
 * field Injections.- Create the object at runtime, from a field references
 * is mandatory to use @Inject. (Andriod components and external libraries )
 *
 */

class NYCViewModel(private val repository: Repository): ViewModel(){

}





