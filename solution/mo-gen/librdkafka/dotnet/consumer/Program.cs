using System;
using System.Threading;
using Confluent.Kafka;

class Program
{
    public static void Main(string[] args)
    {
        Console.WriteLine(">>> Starting .NET Consumer - reading from \"test-topic\"");
        string topicName = "test-topic";
        var conf = new ConsumerConfig
        { 
            GroupId = "test-consumer-group",
            BootstrapServers = "kafka:9092",
            AutoOffsetReset = AutoOffsetReset.Earliest,
            PluginLibraryPaths = "monitoring-interceptor"
        };

        bool consuming = true;
        using (var c = new ConsumerBuilder<string, string>(conf)
            // The client will automatically recover from non-fatal errors. You typically
            // don't need to take any action unless an error is marked as fatal.
        .SetErrorHandler((_, e) => consuming = !e.IsFatal)
        .Build())
        {
            c.Subscribe(topicName);

            while (consuming)
            {
                try
                {
                    var cr = c.Consume();
                    // Console.WriteLine($"Consumed message '{cr.Value}' at: '{cr.TopicPartitionOffset}'.");
                    Console.Write("+");
                    Thread.Sleep(10);
                }
                catch (ConsumeException e)
                {
                    Console.WriteLine($"Error occured: {e.Error.Reason}");
                }
            }
            
            c.Close();
            Console.WriteLine("\r\n<<< Ending .NET Producer");
        }
    }
}