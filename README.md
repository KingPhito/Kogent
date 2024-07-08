<p align="center">
  <img src="./docs/images/logo.png" alt="Kogent Logo" width="200"/>
</p>

# What is Kogent?
Kogent is an open-source Kotlin framework for building RAG pipelines. Kogent enables developers and non-technical 
users alike to harness the power of their data. By seamlessly integrating with diverse data sources, leveraging 
cutting-edge language models and indexing tools, and employing sophisticated retrieval techniques, Kogent offers a natural 
language interface to unlock valuable insights, streamline workflows, and accelerate application development.

# Why Kogent?

# Core Components
Kogent is built on top of the following four core components:
1. **Data Connectors**: Kogent supports a wide range of data sources, including databases, APIs, and file systems. 
   Users can define their data sources and a base query to retrieve data from them. This allows Kogent to index and 
    retrieve data from multiple sources in a unified manner.
2. **Indexing Engine**: Keeping with the theme of flexibility, Kogent allows users to choose from a variety of 
   indexing engines to suit their needs. The indexing engine is responsible for processing and storing the data 
   retrieved from the data sources in a format that is optimized for search and retrieval.
3. **Retrieval Engine**: The Retriever is the workhorse of Kogent, responsible for finding relevant information within 
   the index based on the user's query. It's designed to be flexible and adaptable to different types of indices and 
   retrieval strategies. It provides a context for the language model to generate responses.
4. **Query Engine**: The Query Engine is the coordinator of the system, responsible for processing user queries, 
   interfacing with the retrieval engine, and generating responses. It leverages a language model to understand 
   user queries and provide meaningful responses. Kogent supports multiple language models, in keeping with flexibility. 

