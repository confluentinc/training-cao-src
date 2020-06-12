using System;
using System.IO;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Collections.Generic;
using Confluent.Kafka;

namespace app
{
    public class Program
    {
        public static async Task Main(string[] args)
        {
            Console.WriteLine(">>> Starting .NET Producer - writing to \"test-topic\"");
            string brokerList = "kafka:9092";
            string topicName = "test-topic";

            var config = new ProducerConfig { 
                BootstrapServers = brokerList,
                PluginLibraryPaths = "monitoring-interceptor"
            };

            using (var producer = new ProducerBuilder<string, string>(config).Build())
            {
                var cancelled = false;
                Console.CancelKeyPress += (_, e) => {
                    e.Cancel = true; // prevent the process from terminating.
                    cancelled = true;
                    Console.WriteLine("\r\n<<< Ending .NET Producer");
                };

                var i = 0;
                while (!cancelled)
                {
                    i++;
                    var key = $"cc-a{i}";
                    var val = $"value-{i}";
                    try
                    {
                        Console.Write(".");
                        var deliveryReport = await producer.ProduceAsync(topicName, new Message<string, string> { Key = key, Value = val });
                        //Console.WriteLine($"delivered to: {deliveryReport.TopicPartitionOffset}");
                    }
                    catch (KafkaException e)
                    {
                        Console.WriteLine($"failed to deliver message: {e.Message} [{e.Error.Code}]");
                    }
                    Thread.Sleep(3);
                }
            }
        }
    }
}