<p align="center">
  <img src="./docs/images/logo.png" alt="Kogent Logo" width="200"/>
</p>

# What is Kogent?
Kogent is an open-source Kotlin framework for building RAG pipelines. Kogent enables developers to harness the power of their data. 
By seamlessly integrating with diverse data sources, leveraging cutting-edge language models and indexing tools, and 
employing sophisticated retrieval techniques, Kogent offers a natural language interface to unlock valuable insights, 
streamline workflows, and accelerate application development.
# Why Kogent?
* Kogent is a Kotlin Multiplatform library. This means it can be integrated into a wide range of applications, including
Android, iOS, desktop,and web applications. 
* Kogent is designed to be flexible, modular, and extensible. It provides a set of core components that can be extended 
and customized to suit a wide range of use cases. Whether you're building a chatbot, a search engine, or a data analysis
tool, Kogent helps you build the pipeline with the tools you want to use.
# Core Components
Kogent is built on top of the following four core components:
1. **Data Connectors**: Kogent supports a wide range of data sources, including databases, APIs, and file systems. 
   Users can define their data sources and a base query to retrieve data from them. This allows Kogent to index and 
   retrieve data from multiple sources in a unified manner.
2. **Indexing**: Keeping with the theme of flexibility, Kogent allows users to choose from a variety of 
   indexing tools, including an inverted index Elasticsearch, or a vector database like Milvus. The indexing component
    is responsible for storing data and performing semantic search on user queries.
3. **Retriever**: The Retriever is  responsible for finding relevant information within 
   the index based on the user's query. It's designed to be flexible and utilize several retrieval strategies. It 
   provides a context for the language model to generate responses.
4. **Query Engine**: The Query Engine is the coordinator of the system, responsible for processing user queries, 
   interfacing with the retriever, and generating responses. After receiving context from the retriever,
   the Query Engine passes it to the language model along with the user query to generate a response.

